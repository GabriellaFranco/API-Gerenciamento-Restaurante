package com.restaurant.restaurantManagement.repository;

import com.restaurant.restaurantManagement.enums.UserProfile;
import com.restaurant.restaurantManagement.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findByEmail(String email);

    List<User> findByProfile(UserProfile profile);

    @Query("""
    SELECT u FROM User u
    WHERE (:email IS NULL OR u.email = :email)
      AND (:cpf IS NULL OR u.cpf = :cpf)
      AND (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:phone IS NULL OR u.phone = :phone)
      AND (:profile IS NULL OR u.profile = :profile)
""")
    List<User> findByFilters(
            @Param("email") String email,
            @Param("cpf") String cpf,
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("profile") UserProfile profile
    );

}
