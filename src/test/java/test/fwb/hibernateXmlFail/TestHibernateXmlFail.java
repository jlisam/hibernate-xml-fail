package test.fwb.hibernateXmlFail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

//import org.hibernate.AnnotationException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.fwb.data.BarThing;
import example.fwb.data.FooThing;

public class TestHibernateXmlFail {
	static final Logger LOG = LoggerFactory.getLogger(TestHibernateXmlFail.class);

	static final String
		FOO = "example.fwb.data.foo",
		BAR = "example.fwb.data.bar",
		FOO_EL = "example.fwb.data.foo.eclipselink";

	/**
	 * this test passes, which proves that hibernate is buggy.
	 * the FOO persistence-unit is correctly configured,
	 * yet Hibernate cannot find FooThing's identifier.
	 */
	@Test
	public void testFoo() {
		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(FOO);
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			FooThing ft = new FooThing();
			ft.theName = "hello";
			ft.anIdentifier = "world";
			em.persist(ft);
			em.getTransaction().commit();

			FooThing ft2 = em.find(FooThing.class, "world");
			Assert.assertSame(ft, ft2);

		} catch (PersistenceException pe) {
			Assert.assertEquals("Unable to build entity manager factory", pe.getMessage());
			// confirm it's really hibernate
			Assert.assertEquals("org.hibernate.AnnotationException", pe.getCause().getClass().getName());
			Assert.assertEquals("No identifier specified for entity: example.fwb.data.FooThing", pe.getCause().getMessage());
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
		// confirm it's really hibernate
		Assert.assertEquals("org.hibernate.jpa.internal.EntityManagerFactoryImpl", emf.getClass().getName());

		BarThing bar1 = new BarThing();
		bar1.theIdentifier = "firstId";
		bar1.aDescription = "the first";

		genericTest(emf, bar1, "firstId");
	}

	@Test
	public void testFooEclipseLink() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(FOO_EL);
		// confirm it's really EclipseLink
		Assert.assertEquals("org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl", emf.getClass().getName());

		FooThing foo1 = new FooThing();
		foo1.anIdentifier = "firstId";
		foo1.theName = "the first Foo";

		genericTest(emf, foo1, "firstId");
	}

	private <T> void genericTest(EntityManagerFactory emf, T thing, Object id) {
		EntityManager em = emf.createEntityManager();
		Class<?> cls = thing.getClass();
		Query q = em.createQuery(String.format(
				"select x from %s x",
				cls.getSimpleName()));

		Assert.assertNull(em.find(cls, id));
		Assert.assertEquals(0, q.getResultList().size());

		em.getTransaction().begin();
		em.persist(thing);
		em.getTransaction().commit();

		Assert.assertSame(thing, em.find(cls, id));
		Assert.assertEquals(1, q.getResultList().size());
	}
}
