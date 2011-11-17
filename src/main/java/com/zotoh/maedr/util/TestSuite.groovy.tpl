package test;

import org.junit.*;
import static org.junit.Assert.*;
 
class TestSuite {

    @BeforeClass
    public static void iniz() {
    }    

    @AfterClass
    public static void finz()    {
    }    

    @Before
    public void open() {
    }    

    @After
    public void close() {
    }

    @Test
    public void testDummy() throws Exception {
        println("test OK");
      assertTrue(this != null);
    }

    def TestSuite() {}
}

