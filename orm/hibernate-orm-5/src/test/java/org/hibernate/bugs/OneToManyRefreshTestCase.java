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

import static org.junit.Assert.assertFalse;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.model.Invoice;
import org.hibernate.bugs.model.Line;
import org.hibernate.bugs.model.Tax;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

public class OneToManyRefreshTestCase extends BaseCoreFunctionalTestCase {
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Invoice.class, Line.class, Tax.class };
	}

	// Add in any settings that are specific to your test. See
	// resources/hibernate.properties for the defaults.
	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());
		// configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	@Test
	public void oneToManyRefreshCascadeAllTest() throws Exception {
		createData();
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		Invoice invoice = s.get(Invoice.class, 1);

		assertFalse("Taxes are not initialized before refresh", Hibernate.isInitialized(invoice.getTaxes()));
		assertFalse("Lines are not initialized before refresh", Hibernate.isInitialized(invoice.getLines()));

		s.refresh(invoice);

		assertFalse("Taxes are not initialized before refresh", Hibernate.isInitialized(invoice.getTaxes()));

		// this fails, note that after refresh taxes don't get initialized but lines do
		assertFalse("Lines are not initialized before refresh", Hibernate.isInitialized(invoice.getLines()));

		tx.commit();
		s.close();
	}

	private void createData() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		Invoice invoice = new Invoice("An invoice for John Smith");
		s.save(invoice);

		s.save(new Line("1 pen - 5€", invoice));
		s.save(new Tax("21%", invoice));

		tx.commit();
		s.close();
	}
}
