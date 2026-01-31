package com.alessandragodoy.accountms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing an Account.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer accountId;

	@Column(nullable = false, unique = true)
	private String accountNumber;

	@Column(nullable = false)
	private double balance;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountType accountType;

	@Column(nullable = false)
	private Integer customerId;

	@Column(nullable = false, updatable = false)
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private LocalDateTime updateDate;

	@Column(nullable = false)
	private boolean active;

	@PrePersist
	protected void onCreate() {
		creationDate = LocalDateTime.now();
		updateDate = LocalDateTime.now();
		active = true;
	}

	@PreUpdate
	protected void onUpdate() {
		updateDate = LocalDateTime.now();
	}

}
