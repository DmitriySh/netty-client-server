package ru.shishmakov.server;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for JUnit test classes.
 *
 * @author Dmitriy Shishmakov
 */
public class TestBase {

    @Rule
    public TestName testName = new TestName();

    /**
     * Logger used by test.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp() {
        logTestStart();
    }

    private void logTestStart() {
        logger.info("Running test \"{}\"", testName.getMethodName());
    }

}
