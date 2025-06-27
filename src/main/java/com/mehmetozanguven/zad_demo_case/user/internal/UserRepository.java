package com.mehmetozanguven.zad_demo_case.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("FROM User us WHERE us.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean isUserExistsByEmail(@Param("email") String email);

    @Query("FROM User ue WHERE ue.email IN :userEmails ")
    List<User> findUsersByEmails(List<String> userEmails);

    @Query("FROM User ue WHERE ue.id IN :userIds ")
    List<User> findUsersByIds(List<String> userIds);
}
