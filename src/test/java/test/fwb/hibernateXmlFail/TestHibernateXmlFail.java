package test.fwb.hibernateXmlFail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.hibernate.AnnotationException;
import org.junit.Assert;
import org.junit.Test;

import example.fwb.data.BarThing;

public class TestHibernateXmlFail {
	static final String
		FOO = "example.fwb.data.foo",
		BAR = "example.fwb.data.bar";
	
	/**
	 * this test passes, which proves that hibernate is buggy.
	 * the FOO persistence-unit is correctly configured,
	 * yet Hibernate cannot find FooThing's identifier.
	 */
	@Test(expected=PersistenceException.class)
	public void testFoo() {
		try {
			Persistence.createEntityManagerFactory(FOO);
			
			// unfortunately, it doesn't get here
			Assert.fail();
		} catch (PersistenceException pe) {
			Assert.assertEquals("Unable to build entity manager factory", pe.getMessage());
			Assert.assertTrue(pe.getCause() instanceof AnnotationException);
			Assert.assertEquals("No identifier specified for entity: example.fwb.data.FooThing", pe.getCause().getMessage());
			throw pe;
		}
	}
	
	/**
	 * an example using annotation-id instead of xml-id,
	 * but otherwise analogous, suggesting hibernate itself is working,
	 * and the only problem is xml-id tag recognition.
	 */
	@Test
	public void testBar() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(BAR);
		EntityManager em = emf.createEntityManager();
		
		BarThing bar1 = new BarThing();
		bar1.theIdentifier = "firstId";
		bar1.aDescription = "the first";
		
		em.getTransaction().begin();
		em.persist(bar1);
		em.getTransaction().commit();
		
		Assert.assertSame(bar1, em.find(BarThing.class, "firstId"));
		Assert.assertEquals(1, em.createQuery("from BarThing").getResultList().size());
	}
}
