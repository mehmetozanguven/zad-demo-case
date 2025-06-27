package com.mehmetozanguven.zad_demo_case.account.internal.account;

import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("SELECT CASE WHEN COUNT(acc) > 0 THEN true ELSE false END FROM Account acc WHERE acc.userId = :userId AND acc.accountType = :accountType ")
    boolean hasUserAccountForType(String userId, AccountType accountType);

    @Query("SELECT COUNT(DISTINCT a.accountType) FROM Account a WHERE a.userId = :userId")
    long totalDistinctAccountForUser(String userId);

    @Query("FROM Account acc WHERE acc.userId = :userId")
    Set<Account> getUserAccounts(String userId);

    @Query("FROM Account acc WHERE acc.userId = :userId AND acc.id = :accountId")
    Optional<Account> findUserAccount(String userId, String accountId);

    @Query("FROM Account acc WHERE acc.id = :accountId")
    Optional<Account> findUserAccountByAccountId( String accountId);
}
