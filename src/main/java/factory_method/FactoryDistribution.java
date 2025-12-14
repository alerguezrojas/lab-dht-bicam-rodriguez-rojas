package factory_method;

import java.lang.reflect.InvocationTargetException;


import evolutionary_algorithms.complement.Distribution;
import evolutionary_algorithms.complement.DistributionType;
import factory_interface.IFFactoryDistribution;




public class FactoryDistribution implements IFFactoryDistribution {

	public Distribution createDistribution(DistributionType distributiontype) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		String className = "evolutionary_algorithms.complement." + distributiontype.toString();
		Distribution distribution = (Distribution) FactoryLoader.getInstance(className);
		return distribution;
	}
}
