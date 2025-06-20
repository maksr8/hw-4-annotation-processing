package org.example.processor;

import com.google.auto.service.AutoService;
import com.palantir.javapoet.*;
import org.example.annotation.GenerateDtoValidator;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedAnnotationTypes("org.example.annotation.GenerateDtoValidator")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class DtoValidatorProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(GenerateDtoValidator.class)) {
            TypeElement dtoClass = (TypeElement) elem;


            messager.printMessage(Diagnostic.Kind.NOTE,
                    ">>> DtoValidatorProcessor: processing " + dtoClass.getSimpleName());



            String pkg = processingEnv.getElementUtils()
                    .getPackageOf(dtoClass).getQualifiedName().toString();

            String dtoName = dtoClass.getSimpleName().toString();
            String validatorName = dtoName + "Validator";

            TypeSpec.Builder validator = TypeSpec.classBuilder(validatorName)
                    .addModifiers(Modifier.PUBLIC);

            MethodSpec.Builder method = MethodSpec.methodBuilder("validate")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassName.get(dtoClass), "dto")
                    .addException(IllegalArgumentException.class);

            for (VariableElement field : ElementFilter.fieldsIn(dtoClass.getEnclosedElements())) {
                String fld = field.getSimpleName().toString();
                String getter = "get" + Character.toUpperCase(fld.charAt(0)) + fld.substring(1) + "()";

                if (field.getAnnotation(org.example.annotation.NotEmpty.class) != null) {
                    String msg = field.getAnnotation(org.example.annotation.NotEmpty.class).message();
                    method.addStatement(
                            "if (dto.$L == null || dto.$L.isEmpty()) throw new IllegalArgumentException($S)",
                            getter, getter, dtoName + "." + fld + ": " + msg
                    );
                }
                org.example.annotation.MaxLength ml = field.getAnnotation(org.example.annotation.MaxLength.class);
                if (ml != null) {
                    method.addStatement(
                            "if (dto.$L != null && dto.$L.length() > $L) throw new IllegalArgumentException($S)",
                            getter, getter, ml.value(),
                            dtoName + "." + fld + ": " + ml.message()
                    );
                }
            }

            validator.addMethod(method.build());

            JavaFile javaFile = JavaFile.builder(pkg, validator.build()).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }
        return true;
    }
}
