package com.mehmetozanguven.zad_demo_case.core;


import com.mehmetozanguven.zad_demo_case.core.clearDatabase.ClearDatabaseBeforeEach;
import com.mehmetozanguven.zad_demo_case.core.testcontainer.EnableTestcontainers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test-containers")
@EnableTestcontainers
@ClearDatabaseBeforeEach
public class BaseApplicationModuleMockTest {

}
