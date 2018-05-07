/*
 *************************************************************************
 * The contents of this file are subject to the Openbravo  Public  License
 * Version  1.1  (the  "License"),  being   the  Mozilla   Public  License
 * Version 1.1  with a permitted attribution clause; you may not  use this
 * file except in compliance with the License. You  may  obtain  a copy of
 * the License at http://www.openbravo.com/legal/license.html 
 * Software distributed under the License  is  distributed  on  an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific  language  governing  rights  and  limitations
 * under the License. 
 * The Original Code is Openbravo ERP. 
 * The Initial Developer of the Original Code is Openbravo SLU 
 * All portions are Copyright (C) 2008-2016 Openbravo SLU 
 * All Rights Reserved. 
 * Contributor(s):  ______________________________________.
 ************************************************************************
 */

package org.openbravo.dal.core;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.openbravo.base.model.NamingUtil;
import org.openbravo.base.structure.BaseOBObject;

/**
 * The hibernate getter/setter for a dynamic property.
 * 
 * @author mtaal
 */
@SuppressWarnings("rawtypes")
public class OBDynamicPropertyHandler implements PropertyAccessStrategy {
	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException {
		return new Getter(theClass, propertyName);
	}

	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException {
		return new Setter(theClass, propertyName);
	}

	public static class Getter implements org.hibernate.property.access.spi.Getter {
		private static final long serialVersionUID = 1L;
		private static final String ID_GETTER = "getId";

		private String propertyName;
		private Class theClass;

		public Getter(Class theClass, String propertyName) {
			this.theClass = theClass;
			this.propertyName = NamingUtil.getStaticPropertyName(theClass, propertyName);
		}

		@SuppressWarnings("unchecked")
		public Method getMethod() {
			// Property property =
			// ModelProvider.getInstance().getEntity(theClass).getProperty(propertyName);
			String methodName = propertyName;// property.getGetterSetterName();
			methodName = "get" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);

			try {
				return theClass.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException | SecurityException e) {
				String pName = propertyName;
				if (pName.toLowerCase().startsWith("is")) {
					pName = pName.substring(2);
				}
				methodName = "is" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
				try {
					return theClass.getDeclaredMethod(methodName);
				} catch (NoSuchMethodException | SecurityException e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
			}
			return null;
		}

		public Member getMember() {
			return getMethod();
		}

		public String getMethodName() {

			return getMethod().getName();
		}

		public Object get(Object owner) throws HibernateException {
			return ((BaseOBObject) owner).getValue(propertyName);
		}

		public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session)
				throws HibernateException {
			return get(owner);
		}

		public Class getReturnType() {
			return null;
		}
	}

	public static class Setter implements org.hibernate.property.access.spi.Setter {
		private static final long serialVersionUID = 1L;
		private static final String ID_SETTER = "setId";

		private String propertyName;
		private Class theClass;

		public Setter(Class theClass, String propertyName) {
			this.theClass = theClass;
			this.propertyName = NamingUtil.getStaticPropertyName(theClass, propertyName);
		}

		@SuppressWarnings("unchecked")
		public Method getMethod() {
			if (BaseOBObject.ID.equals(propertyName)) {
				try {
					return theClass.getDeclaredMethod(ID_SETTER, String.class);
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				}
			}

			return null;
		}

		public String getMethodName() {
			return null;
		}

		public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException {
			((BaseOBObject) target).setValue(propertyName, value);
		}

	}

	@Override
	public PropertyAccess buildPropertyAccess(final Class theClass, final String propertyName) {
		final OBDynamicPropertyHandler me = this;
		// TODO Auto-generated method stub
		return new PropertyAccess() {

			@Override
			public Setter getSetter() {
				// TODO Auto-generated method stub
				return new Setter(theClass, propertyName);
			}

			@Override
			public PropertyAccessStrategy getPropertyAccessStrategy() {
				// TODO Auto-generated method stub
				return me;
			}

			@Override
			public Getter getGetter() {
				// TODO Auto-generated method stub
				return new Getter(theClass, propertyName);
			}
		};
	}
}
