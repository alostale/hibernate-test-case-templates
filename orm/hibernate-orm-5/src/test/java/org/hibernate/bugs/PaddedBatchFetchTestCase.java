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

import java.util.List;
import java.util.stream.IntStream;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.bugs.model.City;
import org.hibernate.bugs.model.Country;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

public class PaddedBatchFetchTestCase extends BaseCoreFunctionalTestCase {

	// Add your entities here.
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Country.class, City.class };
	}

	@Override
	protected void configure(Configuration configuration) {
		super.configure(configuration);

		configuration.setProperty(AvailableSettings.SHOW_SQL, Boolean.TRUE.toString());
		configuration.setProperty(AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString());

		configuration.setProperty(AvailableSettings.BATCH_FETCH_STYLE, "PADDED");
		configuration.setProperty(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, "15");

		// configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}

	// Add your tests, using standard JUnit.
	@Test
	public void paddedBatchFetchTest() throws Exception {
		createData();

		Session s = openSession();
		Transaction tx = s.beginTransaction();

		List<City> allCities = s.createQuery("from City", City.class).list();

		// this triggers countries to be fetched in batch
		System.out.println(allCities.get(0).getCountry().getName());

		tx.commit();
		s.close();
	}

	private void createData() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		// Having DEFAULT_BATCH_FETCH_SIZE=15
		// results in batchSizes = [15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
		// Let's create 11 countries so batch size 15 will be used with padded values,
		// this causes to have to remove 4 elements from list
		int numberOfCountries = 11;

		IntStream.range(0, numberOfCountries).forEach(i -> {
			Country c = new Country("Country-" + i);
			s.save(c);
			s.save(new City("City" + i, c));
		});

		tx.commit();
		s.close();
	}
}
