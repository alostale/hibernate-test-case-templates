/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.persistence.LockModeType;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.model.Account;
import org.hibernate.bugs.model.Deposit;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggressivePessimisticWriteTestCase extends BaseCoreFunctionalTestCase {
	private static final Logger log = LoggerFactory.getLogger(AggressivePessimisticWriteTestCase.class);
	private List<String> completionOrder = new ArrayList<>();

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Account.class, Deposit.class };
	}

	// Add in any settings that are specific to your test. See
	// resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
	}

	@Test
	public void pessimisticWriteLockInPGisTooAgressiveTest() throws Exception {
		Account newAcct = createAnAccount();

		ExecutorService exec = Executors.newFixedThreadPool(2);
		List<Future<Void>> executions = exec.invokeAll(Arrays.asList( //
				this::lockAccount2Secs, // This thread creates a lock in Account during 2 secs
				() -> createDeposit(newAcct) // This thread waits 1 sec and creates a deposit in same acct
		));

		for (Future<Void> execution : executions) {
			// get results, just to throw any exception that might have occurred during
			// execution
			execution.get();
		}

		log.info("Done {}", completionOrder);

		// Succeeds in Oracle, Fails in PostgreSQL
		assertThat("Completion order", completionOrder, is(Arrays.asList("T2", "T1")));
	}

	private Account createAnAccount() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Account acct = new Account("My Bank Account");
		s.save(acct);
		tx.commit();
		s.close();
		return acct;
	}

	private Void lockAccount2Secs() throws Exception {
		try (Session s = openSession()) {
			Transaction tx = s.beginTransaction();

			Account acct = s.createQuery("from Account", Account.class).setLockMode(LockModeType.PESSIMISTIC_WRITE)
					.uniqueResult();
			log.info("Acquired lock on {}", acct);

			// Let's hold the lock for a couple of secs
			TimeUnit.SECONDS.sleep(2L);

			tx.commit();
			completionOrder.add("T1");
			return null;
		} catch (Exception e) {
			log.error("Error", e);
			throw e;
		} finally {
			log.info("Done");
		}
	}

	private Void createDeposit(Account acct) throws Exception {
		try (Session s = openSession()) {
			// let's wait to allow the other thread to acquire the lock
			TimeUnit.SECONDS.sleep(1L);

			Transaction tx = s.beginTransaction();

			// Account acct = s.get(Account.class, 1);
			s.save(new Deposit(acct, BigDecimal.valueOf(100L)));

			log.info("Flushing new deposit");
			s.flush();

			tx.commit();
			completionOrder.add("T2");
			return null;
		} catch (Exception e) {
			log.error("Error", e);
			throw e;
		} finally {
			log.info("Done");
		}
	}
}
