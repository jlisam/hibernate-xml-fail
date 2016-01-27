# hibernate xml fail

this is a minimal example of hibernate failing to correctly interpret an xml (non-annotation) mapping.
it includes:
* an annotated persistence unit
* an xml-specified persistence unit

the results are in:

	src/test/java/test.fwb.hibernateXmlFail.TestHibernateXmlFail

it contains:
*	a passing test demonstrating hibernate's failure to build the persistence unit.
*	another passing test to demonstrate that
	hibernate and my configuration are otherwise working,
	as long as the entity in question is annotated.
*	finally, a test configured to use EclipseLink on the xml-mapped bean,
	which passes with flying colors
