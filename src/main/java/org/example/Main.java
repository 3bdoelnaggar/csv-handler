package org.example;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.*;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {
        File file = new File("src//main//resources//input.csv");


        TypeSpec.Builder builder = TypeSpec.classBuilder("CSVModel")
                .addModifiers(Modifier.PUBLIC);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));


            String firstLine = bufferedReader.readLine();
            String[] keys = firstLine.split(",");
            String[] values = bufferedReader.readLine().split(",");
            for (int i = 0; i < keys.length; i++) {
                var value = "";
                if (i < values.length) {
                    value = values[i];
                }
                FieldSpec field = FieldSpec.builder(String.class, keys[i])
                        .addModifiers(Modifier.PUBLIC)
                        .initializer("$S", value)
                        .build();
                builder.addField(field);
            }

            TypeSpec typeSpec = builder.build();

            JavaFile javaFile = JavaFile.builder("org.example", typeSpec).build();

            javaFile.writeTo(new File("src//main//java/"));


            generate(new Parameter("name","said"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void generate(Parameter paramter) {
        File file = new File("src//main//resources//output.csv");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder header = new StringBuilder();
        Class<CSVModel> aClass = CSVModel.class;
        for (Field field : aClass.getFields()) {
            header.append(field.getName());
            header.append(",");
        }
        header.deleteCharAt(header.length()-1);


        try {
            fileWriter.write(header.toString());
            fileWriter.write("\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        CSVModel csvModel = new CSVModel();
        StringBuilder line = new StringBuilder();

        for (Field field : aClass.getFields()) {
            try {
                if (field.getName().equals(paramter.key())) {
                    line.append(paramter.value());
                } else {
                    line.append(field.get(csvModel));
                }
                line.append(",");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        line.deleteCharAt(line.length()-1);
        try {
            fileWriter.write(line.toString());
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}


