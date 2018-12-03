package org.hibernate.bugs.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column
	private String name;

	@Column
	private BigDecimal balance = BigDecimal.ZERO;

	@OneToMany(mappedBy = "account")
	private List<Deposit> deposits;

	public Account() {
	}

	public Account(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + " [" + balance + "]";
	}
}
