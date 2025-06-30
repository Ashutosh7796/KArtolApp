package com.spring.jwt.Event;

import com.spring.jwt.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.query.Param;


public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByDate(@Param("date") LocalDate date);
}