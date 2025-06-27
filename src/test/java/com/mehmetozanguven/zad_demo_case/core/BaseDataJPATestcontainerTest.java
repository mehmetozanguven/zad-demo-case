package com.mehmetozanguven.zad_demo_case.core;

import com.mehmetozanguven.zad_demo_case.core.clearDatabase.ClearDatabaseBeforeEach;
import com.mehmetozanguven.zad_demo_case.core.testcontainer.EnableTestcontainers;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ClearDatabaseBeforeEach
@EnableTestcontainers
@ActiveProfiles("test-containers")
public abstract class BaseDataJPATestcontainerTest {
}
