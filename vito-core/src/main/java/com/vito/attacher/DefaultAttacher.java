package com.vito.attacher;


import com.vito.exception.AttacherException;import com.vito.framework.Processor;
import com.vito.framework.annotations.mark.MarkId;
import javassist.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DefaultAttacher  implements Processor<Map<String,List<String>>>{

    private final String MARK_CLASS = "markClass";
    private final String ID_MARKER = "id";

    @Override
    public Map<String, List<String>> process(Map<String, List<String>> config) throws Exception {

        if(!config.containsKey(MARK_CLASS)){
            throw new AttacherException("No Mark Classes found");
        }

        for (String strMarkClass : config.get(MARK_CLASS)) {
            Class markClazz = Class.forName(strMarkClass);
            CtClass markCtClass = ClassPool.getDefault().get(strMarkClass);
            attachMarkClass(markCtClass,markClazz);

        }


        return config;
    }

    private void attachMarkClass(CtClass markCtClass,Class markClazz) throws ClassNotFoundException, AttacherException, CannotCompileException, NotFoundException, IOException {

        boolean idExistOnAnnotation = false;
        boolean idExistOnField = false;
        boolean setterExist = false;
        String idFieldName = "";
        //1.first check to see if there is a identification annotation
        for (CtField ctField : markCtClass.getDeclaredFields()) {

            for (Object annotation : ctField.getAnnotations()) {
                if(annotation instanceof MarkId) {
                    idExistOnAnnotation = true;
                    idFieldName = ctField.getName();
                }
            }

            if(!idExistOnAnnotation){
                if(ctField.getName().toLowerCase().equals(ID_MARKER)){
                    idExistOnField = true;
                    idFieldName = ctField.getName();
                }
            }



        }

        if(!idExistOnAnnotation && !idExistOnField){
            throw new AttacherException("No Identification field found");
        }

        //check to see if this method has a Setter
        for (CtMethod ctMethod : markCtClass.getDeclaredMethods()) {

            String methodName = ctMethod.getName();
            if(methodName.startsWith("set")){
                String setterFieldName = methodName.substring(3).toLowerCase();
                if(setterFieldName.equals(idFieldName)){
                    setterExist = true;
                  //  ctMethod.insertAfter("{System.out.println(\"Hello World\");}");
                }
            }
        }

        if(!setterExist){
            throw new AttacherException("No Setter found for Annotatted field");
        }

        //attach code to this setter
        markCtClass.toClass(markClazz.getClassLoader(),markClazz.getProtectionDomain());

    }
}
