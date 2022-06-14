package cs350s22.component.ui.parser;

import java.io.IOException;

import cs350s22.component.sensor.mapper.MapperEquation;
import cs350s22.component.sensor.mapper.MapperInterpolation;
import cs350s22.component.sensor.mapper.function.equation.*;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolationMap;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolatorLinear;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolatorSpline;
import cs350s22.component.sensor.mapper.function.interpolator.loader.MapLoader;
import cs350s22.support.Filespec;
import cs350s22.support.Identifier;

public class ParseMapper {

	public static Object parseMapperCommand(A_ParserHelper parserHelper, String input) throws IOException {
        String[] command = input.split(" ");
        String type;	        
        double scaleValue = 0;
        double normValueOne = 0;
        double normValueTwo = 0;
        
      	type = command[3];
	    String val = command[2];
	    Identifier id = Identifier.make(val); 
	    
        	switch (type) {
	        	case "EQUATION":
	        		if(command[4].contains("PASSTHROUGH")) {
	        			EquationPassthrough ep = new EquationPassthrough();
	        			MapperEquation me = new MapperEquation(ep);
	        			parserHelper.getSymbolTableMapper().add(id, me);
	        		}
	        		if(command[4].contains("SCALE")) {
	        			scaleValue = Double.parseDouble(command[5]);
	        			EquationScaled es = new EquationScaled(scaleValue);
	        			MapperEquation me = new MapperEquation(es);
	        			parserHelper.getSymbolTableMapper().add(id, me);
	        		}
	        		if(command[4].contains("NORMALIZE")) {
	        			normValueOne = Double.parseDouble(command[5]);
	        			normValueTwo = Double.parseDouble(command[6]);
	        			EquationNormalized es = new EquationNormalized(normValueOne, normValueTwo);
	        			MapperEquation me = new MapperEquation(es);
	        			parserHelper.getSymbolTableMapper().add(id, me);
	        		}
	        		break;
	        	case "INTERPOLATION":
	        			Filespec filepath = new Filespec(command[6]);
	        			MapLoader mapLoader = new MapLoader(filepath);
	        			InterpolationMap interMap = mapLoader.load();
		        		if(command[4].contains("LINEAR")) {
		        			InterpolatorLinear interpolator = new InterpolatorLinear(interMap);
		        			MapperInterpolation mapInter = new MapperInterpolation(interpolator);
		        			parserHelper.getSymbolTableMapper().add(id, mapInter);
		        		}
		        		else if(command[4].contains("SPLINE")) {
		        			InterpolatorSpline interpolator = new InterpolatorSpline(interMap);
		        			MapperInterpolation mapInter = new MapperInterpolation(interpolator);
		        			parserHelper.getSymbolTableMapper().add(id, mapInter);
		        		}
	        		break;
	        }
        
        return null;
	}

}
