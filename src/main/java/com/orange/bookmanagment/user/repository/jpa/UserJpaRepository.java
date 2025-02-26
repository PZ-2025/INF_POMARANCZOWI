package com.orange.bookmanagment.user.repository.jpa;

import com.orange.bookmanagment.user.model.User;
import com.orange.bookmanagment.user.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserJpaRepository extends JpaRepository<User,Long>, UserRepository {

}
