package ca.team5406.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.*;

/**
 * ConstantsBase
 * <p>
 * Base class for storing robot constants. Anything stored as a public static
 * field will be reflected and be able to set externally
 *
 * @author Tom Bottiglieri
 */
public abstract class ConstantsBase {
    HashMap<String, Boolean> modifiedKeys = new HashMap<String, Boolean>();

    public abstract String getFileLocation();

    public static class Constant {
        public String name;
        public Class<?> type;
        public Object value;

        public Constant(String name, Class<?> type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }

    public File getFile() {
        String filePath = getFileLocation();
        filePath = filePath.replaceFirst("^~", System.getProperty("user.home"));
        return new File(filePath);
    }

    private boolean setConstantRaw(String name, Object value) {
        boolean success = false;
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                try {
                    Object current = field.get(this);
                    field.set(this, value);
                    success = true;
                    if (!value.equals(current)) {
                        modifiedKeys.put(name, true);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    System.out.println("Could not set field: " + name);
                }
            }
        }
        return success;
    }

    public Object getValueForConstant(String name) throws Exception {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                try {
                    return field.get(this);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new Exception("Constant not found");
                }
            }
        }
        throw new Exception("Constant not found");
    }

    public Constant getConstant(String name) {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                try {
                    return new Constant(field.getName(), field.getType(), field.get(this));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Constant("", Object.class, 0);
    }

    public void loadFromFileASDFG() {
		try {
			File file = getFile();
			FileReader fr = new FileReader(file);
		    BufferedReader br = new BufferedReader(fr);
		    String line;
		    
			while((line = br.readLine()) != null){
				String[] parts = line.replaceAll(" ", "").split(":");
				String name = parts[0];
				double value = Double.parseDouble(parts[1]);
				
				if(name.startsWith("#")) continue; //Marked to skip
				if(name.length() <= 2) continue; //Name too short
				
				if(setConstantRaw(name, value)){
					System.out.println("Setting " + name + " to " + value);
				}
				else{
					System.out.println("Error: Could not find constant: " + name);
				}
			}
			
		    br.close();
		    fr.close();
		}
		catch (Exception e){
			System.out.println("Error: Could not load constants!");
		}
    }

}