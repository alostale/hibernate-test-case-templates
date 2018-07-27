package org.hibernate.bugs.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "invoice")
public class Invoice {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "description")
	private String description;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<Line> lines;

	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<Tax> taxes;

	public Invoice() {
	}

	public Invoice(String description) {
		this.description = description;
	}

	public List<Line> getLines() {
		return lines;
	}

	public List<Tax> getTaxes() {
		return taxes;
	}
}
