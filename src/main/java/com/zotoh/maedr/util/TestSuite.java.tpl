package test;

import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;
import org.junit.*;
import java.lang.annotation.Annotation;

public class TestSuite {
    
    public static junit.framework.Test suite()     {
        return 
        new JUnit4TestAdapter(TestSuite.class);
    }

    @BeforeClass
    public static void iniz() throws Exception    {                    
    }    

    @AfterClass
    public static void finz()    {
    }    

    @Before
    public void open() throws Exception    {
    }    

    @After
    public void close() throws Exception    {        
    }

    @Test
    public void testDummy() throws Exception {
        System.out.println("test OK");
      assertTrue(this != null);
    }


    public TestSuite() {}
    
}

