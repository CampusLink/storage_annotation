package com.campus.system.storage_annotation.processor;

import com.campus.system.storage_annotation.annotation.Constants;
import com.campus.system.storage_annotation.annotation.DateProperty;
import com.campus.system.storage_annotation.annotation.DoubleProperty;
import com.campus.system.storage_annotation.annotation.Enity;
import com.campus.system.storage_annotation.annotation.LongProperty;
import com.campus.system.storage_annotation.annotation.IntProperty;
import com.campus.system.storage_annotation.annotation.Id;
import com.campus.system.storage_annotation.annotation.StringProperty;
import com.campus.system.storage_annotation.property.Property;
import com.campus.system.storage_annotation.util.TextUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(
        {Constants.EnityPath, Constants.DatePath, Constants.DoublePath
                , Constants.IdPath, Constants.IntPath
                , Constants.LongPath, Constants.StringPath})
public class EnityProcessor extends AbstractProcessor {
    private HashMap<Element, List<FieldAndAnnotation>> enities;
    public final static String EnityCursorPath = "com.campus.system.menu.processor";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        enities = new HashMap();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        dealEnity(roundEnv);
        createJavaFile();
        return true;
    }

    private void dealEnity(RoundEnvironment roundEnv) {
        Set<? extends Element> enityElements = roundEnv.getElementsAnnotatedWith(Enity.class);
        for (Element element : enityElements) {
            // 获取 Enity 注解的值
            List<? extends Element> list = element.getEnclosedElements();

            for (Element item : list) {
                if (item instanceof VariableElement) {
                    dealId((VariableElement) item, element);
                    dealDate((VariableElement) item, element);
                    dealDouble((VariableElement) item, element);
                    dealInt((VariableElement) item, element);
                    dealLong((VariableElement) item, element);
                    dealString((VariableElement) item, element);
                }
            }
        }
    }

    private void dealDate(VariableElement element, Element enity) {

        DateProperty dateProperty = element.getAnnotation(DateProperty.class);
        if (dateProperty == null) {
            return;
        }
        List list = checkList(enities.get(enity));
        list.add(new FieldAndAnnotation(element, dateProperty));
        enities.put(enity, list);
    }

    private void dealDouble(VariableElement element, Element enity) {
        DoubleProperty doubleProperty = element.getAnnotation(DoubleProperty.class);
        if (doubleProperty == null) {
            return;
        }
        List list = checkList(enities.get(enity));
        list.add(new FieldAndAnnotation(element, doubleProperty));
        enities.put(enity, list);
    }

    private void dealId(VariableElement element, Element enity) {
        Id id = element.getAnnotation(Id.class);
        if (id == null) {
            return;
        }
        List list = checkList(enities.get(enity));
        list.add(new FieldAndAnnotation(element, id));
        enities.put(enity, list);
    }

    private void dealInt(VariableElement element, Element enity) {
        IntProperty intProperty = element.getAnnotation(IntProperty.class);
        if (intProperty == null) {
            return;
        }
        List list = checkList(enities.get(enity));
        list.add(new FieldAndAnnotation(element, intProperty));
        enities.put(enity, list);
    }

    private void dealLong(VariableElement element, Element enity) {
        LongProperty longProperty = element.getAnnotation(LongProperty.class);
        if (longProperty == null) {
            return;
        }
        List list = checkList(enities.get(enity));
        list.add(new FieldAndAnnotation(element, longProperty));
        enities.put(enity, list);
    }

    private void dealString(VariableElement element, Element enity) {
        StringProperty stringProperty = element.getAnnotation(StringProperty.class);
        if (stringProperty == null) {
            return;
        }
        List list = checkList(enities.get(enity));
        list.add(new FieldAndAnnotation(element, stringProperty));
        enities.put(enity, list);
    }

    private List checkList(List list) {
        if (list == null) {
            list = new ArrayList();
        }
        return list;
    }

    private void createJavaFile() {
        Iterator<Element> it = enities.keySet().iterator();

        while (it.hasNext()) {
            Element element = it.next();

            List<FieldAndAnnotation> fileds = enities.get(element);

            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(element.getSimpleName().toString() + "_")
                    .superclass(ParameterizedTypeName.get(
                            ClassName.get("com.campus.system.storage.box", "EnityCursor"),
                            ClassName.get((TypeElement) element)))
                    .addModifiers(Modifier.PUBLIC);

            //1.创建各个字段
            for (FieldAndAnnotation item : fileds) {
                typeBuilder.addField(createPropertyField(item).build());
            }
            //2.创建构造函数
            typeBuilder.addMethod(createConstruct(element, fileds).build());
            //3.创建各个字段的set方法
            for (FieldAndAnnotation item : fileds) {
                typeBuilder.addMethod(createPropertySetter(item, element).build());
            }
            System.out.println("parseToBean---->");
            //4.创建将ResultSet转化为实体的方法
            typeBuilder.addMethod(createParseToBean(element, fileds).build());
            System.out.println("parseToBean---->DONE");

            System.out.println("saveBean---->");
            //4.创建将ResultSet转化为实体的方法
            typeBuilder.addMethod(createSaveBean(element, fileds).build());
            System.out.println("saveBean---->DONE");

            JavaFile javaFile = JavaFile.builder(EnityCursorPath, typeBuilder.build())
                    .build();
            Filer filer = processingEnv.getFiler();
            try {
                javaFile.writeTo(new File(System.getProperty("user.dir") + "/src/main/java"));
                System.out.println("生产类成功");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("生产类异常：" + e.getMessage());
            }
        }
    }

    private FieldSpec.Builder createPropertyField(FieldAndAnnotation item) {
        String name = item.mElement.getSimpleName().toString();
        FieldSpec.Builder builder = null;
        String basePath = "com.campus.system.storage_annotation.property";
        if (item.mAnnotation instanceof Id) {
            Id id = (Id) item.mAnnotation;
            ClassName className = ClassName.get(basePath, "Id");
            builder = FieldSpec.builder(className, name)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(" new $T(\"" + id.nameInDb() + "\", \""
                                    + id.desc() + "\"," + id.defaultValue() + ", " + id.autoIncrease() + ")",
                            className);
        } else if (item.mAnnotation instanceof DateProperty) {
            DateProperty dateProperty = (DateProperty) item.mAnnotation;
            ClassName className = ClassName.get(basePath, "DateProperty");
            builder = FieldSpec.builder(className, name)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(" new $T(\"" + dateProperty.nameInDb() + "\", \""
                            + dateProperty.desc() + "\", " +
                            "new $T("+dateProperty.defalutValue()[0]+", " +
                            dateProperty.defalutValue()[1]+", "
                            + dateProperty.defalutValue()[2] +"))", className, Date.class);
        } else if (item.mAnnotation instanceof DoubleProperty) {
            DoubleProperty doubleProperty = (DoubleProperty) item.mAnnotation;
            ClassName className = ClassName.get(basePath, "DoubleProperty");
            builder = FieldSpec.builder(className, name)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(" new $T(\"" + doubleProperty.nameInDb() + "\", \""
                                    + doubleProperty.desc() + "\"," + doubleProperty.defaultValue() + ")",
                            className);
        } else if (item.mAnnotation instanceof IntProperty) {
            IntProperty intProperty = (IntProperty) item.mAnnotation;
            ClassName className = ClassName.get(basePath, "IntProperty");
            builder = FieldSpec.builder(className, name)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(" new $T(\"" + intProperty.nameInDb() + "\", \""
                            + intProperty.desc() + "\"," + intProperty.defaultValue() + ", "
                            + intProperty.autoIncrease() + ")", className);
        } else if (item.mAnnotation instanceof LongProperty) {
            LongProperty longProperty = (LongProperty) item.mAnnotation;
            ClassName className = ClassName.get(basePath, "LongProperty");
            builder = FieldSpec.builder(className, name)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(" new $T(\"" + longProperty.nameInDb() + "\", \""
                            + longProperty.desc() + "\"," + longProperty.defaultValue() + ")", className);
        } else if (item.mAnnotation instanceof StringProperty) {
            StringProperty stringProperty = (StringProperty) item.mAnnotation;
            ClassName className = ClassName.get(basePath, "StringProperty");
            builder = FieldSpec.builder(className, name)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(" new $T(\"" + stringProperty.nameInDb() + "\", \""
                            + stringProperty.desc() + "\",\"" + stringProperty.defaultValue() + "\", "
                            + stringProperty.length() + ")", className);
        }

        return builder;
    }

    private MethodSpec.Builder createConstruct(Element element, List<FieldAndAnnotation> fileds) {
        Enity enity = element.getAnnotation(Enity.class);
        String tableName = enity.name().trim();
        if(tableName.length() == 0){
            tableName = element.getSimpleName().toString();
        }

        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($S)", tableName)
                .addStatement("mEnityClass = $T.class", ClassName.get((TypeElement) element));

        for (FieldAndAnnotation field : fileds) {
            String name = field.mElement.getSimpleName().toString();
            builder.addStatement("mProperties.add(" + name + ")");
        }

        return builder;
    }

    private MethodSpec.Builder createPropertySetter(FieldAndAnnotation item, Element element) {
        String name = item.mElement.getSimpleName().toString();
        String getMethodName = "get";
        if (item.mAnnotation instanceof Id) {
            getMethodName += "Id";
        } else if (item.mAnnotation instanceof DateProperty) {
            getMethodName += "Date";
        } else if (item.mAnnotation instanceof DoubleProperty) {
            getMethodName += "Double";
        } else if (item.mAnnotation instanceof IntProperty) {
            getMethodName += "Int";
        } else if (item.mAnnotation instanceof LongProperty) {
            getMethodName += "Long";
        } else if (item.mAnnotation instanceof StringProperty) {
            getMethodName += "String";
        }
        MethodSpec.Builder setBuilder = MethodSpec.methodBuilder("set" + TextUtil.upcaseFirstForMethod(name))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addException(ClassName.get("java.lang", "Exception"))
                .addParameter(ClassName.get(element.asType()), "enity")
                .addParameter(ClassName.get("com.campus.system.storage.ResultSet", "ResultSet"), "resultSet")
                .addStatement("enity.set" + TextUtil.upcaseFirstForMethod(name) + "(resultSet." + getMethodName + "(resultSet.findColumn(" + name + ".getNameInDb())))", TextUtil.upcaseFirstForMethod(name))
                .returns(void.class);
        return setBuilder;
    }

    private MethodSpec.Builder createParseToBean(Element element, List<FieldAndAnnotation> fileds) {
        StringBuilder setter = new StringBuilder();
        for (FieldAndAnnotation item : fileds) {
            String name = item.mElement.getSimpleName().toString();
            setter.append("if(keys.contains(" +name+")){\n");
            setter.append("     set" + TextUtil.upcaseFirstForMethod(name) + "(t, resultSet);\n");
            setter.append("}\n");
        }
        MethodSpec.Builder parseResultBuilder = MethodSpec.methodBuilder("parseResult")
                .addModifiers(Modifier.PUBLIC)
                .addException(ClassName.get("java.lang", "Exception"))
                .addParameter(ClassName.get("com.campus.system.storage.ResultSet", "ResultSet"), "resultSet")
                .addParameter(ParameterizedTypeName.get(ClassName.get("java.util", "List"),
                        ClassName.get("com.campus.system.storage_annotation.property", "Property")), "keys")
                .addStatement("$T<$T> list = new $T()", List.class, ClassName.get((TypeElement) element), ArrayList.class)
                .addCode("while(resultSet.next()){\n" +
                        "$T t = new $T();\n" +
                        setter +
                        "list.add(t);\n" +
                        "}\n", ClassName.get((TypeElement) element), ClassName.get((TypeElement) element))
                .addStatement("return list")
                .returns(ParameterizedTypeName.get(ClassName.get("java.util", "List"), ClassName.get((TypeElement) element)));
        return parseResultBuilder;
    }

    private MethodSpec.Builder createSaveBean(Element element, List<FieldAndAnnotation> fileds){
        StringBuilder setter = new StringBuilder();
        setter.append("HashMap<Property, Object> properties = new HashMap();\n");
        for(FieldAndAnnotation item : fileds){
            String name = item.mElement.getSimpleName().toString();

            if (item.mAnnotation instanceof Id || item.mAnnotation instanceof DoubleProperty
                    || item.mAnnotation instanceof IntProperty || item.mAnnotation instanceof LongProperty) {
                setter.append("if(enity.get" + TextUtil.upcaseFirstForMethod(name) + "()>0){\n");
            } else if (item.mAnnotation instanceof StringProperty) {
                setter.append("if(enity.get" + TextUtil.upcaseFirstForMethod(name) + "() != null && enity.get" + TextUtil.upcaseFirstForMethod(name)+"().length() > 0){\n");
            } else if(item.mAnnotation instanceof DateProperty){
                setter.append("if(!com.campus.system.storage_annotation.util.DateUtil.dateIsNull(enity.get" + TextUtil.upcaseFirstForMethod(name)+ "())){\n");
            }
            setter.append("     properties.put("+ name+", enity.get"+TextUtil.upcaseFirstForMethod(name)+"());\n");
            setter.append("}\n");
        }
        setter.append("return properties;");

        MethodSpec.Builder saveBeanBuilder = MethodSpec.methodBuilder("saveBean")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(element.asType()), "enity")
                .addCode(setter.toString())
                .returns(ParameterizedTypeName.get(HashMap.class, Property.class, Object.class));

        return saveBeanBuilder;
    }

    public static class FieldAndAnnotation {
        protected Element mElement;
        protected Annotation mAnnotation;

        public FieldAndAnnotation(Element element, Annotation annotation) {
            mElement = element;
            mAnnotation = annotation;
        }
    }
}
