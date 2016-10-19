//
// Created by Jakub Háva on 13/05/16.
//

#include "JavaUtils.h"
#include "ByteReader.h"
#include "ConstantPool.h"
#include "JavaConst.h"
#include "Field.h"

namespace Distrace {
    namespace JavaUtils {


        unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
            int len = env->GetArrayLength (array);
            unsigned char* buf = new unsigned char[len];
            env->GetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
            return buf;
        }

        bool isAlreadyLoaded(JNIEnv *env, const char *name){
           // jclass byteLoader = env->FindClass("cz/cuni/mff/d3s/distrace/Utils");
           // jmethodID methodLoadClass = env->GetStaticMethodID(byteLoader,"loaded","(Ljava/lang/String;)Z");
           // jstring name_for_java = env->NewStringUTF(name);
          //  auto should_continue = env->CallStaticBooleanMethod(byteLoader, methodLoadClass, name_for_java);
            return false;
        }

        std::string toNameWithDots(std::string name){

            std::string class_name(name);
            std::replace(class_name.begin(), class_name.end(), '/', '.');
            return class_name;
        }

        void readMagicId(ByteReader &reader){
            int magicId = reader.readInt();
            int JVM_CLASSFILE_MAGIC = 0xCAFEBABE;
            if(JVM_CLASSFILE_MAGIC != magicId){
                throw "Magic id is not correct";
            }
        }

        void readVersions(ByteReader &reader){
               short minor = reader.readShort();
               short major = reader.readShort();
        }

        ConstantPool* constant_pool;
        void readConstantPool(ByteReader &reader){
            constant_pool = new ConstantPool(reader);
        }

        void readClassInfo(ByteReader &reader){
           int access_flags = reader.readShort();
            /* Interfaces are implicitely abstract, the flag should be set
             * according to the JVM specification.
             */
            if ((access_flags & JavaConst::ACC_INTERFACE) != 0) {
                access_flags |= JavaConst::ACC_ABSTRACT;
            }
            if (((access_flags & JavaConst::ACC_ABSTRACT) != 0)
                && ((access_flags & JavaConst::ACC_FINAL) != 0)) {
                throw "Class can't be both final and abstract";
            }
            int class_name_index = reader.readShort();
            int superclass_name_index = reader.readShort();
        }

        void readInterfaces(ByteReader &reader){
            int interfaces_count = reader.readShort();
            int *interfaces = new int[interfaces_count];
            for (int i = 0; i < interfaces_count; i++) {
                interfaces[i] = reader.readShort();
            }
            // delete interfaces array
        }

        void readFields(ByteReader &reader){
            int fields_count = reader.readShort();
            Field *fields = new Field[fields_count];
            for (int i = 0; i < fields_count; i++) {
                fields[i] = *(new Field(reader, *constant_pool));
                // use fields[i].getSignature() to read name

            }
        }
        bool forceLoadClass(JNIEnv *env, const char *name, const unsigned char *class_data, jint class_data_len){


            //TODO implement this in C++, so Utils class doesn't have to be loaded
            ByteReader reader(class_data, class_data_len);
            readMagicId(reader);
            readVersions(reader);
            readConstantPool(reader);
            readClassInfo(reader);
            readInterfaces(reader);
            readFields(reader);
           // jclass byteLoader = env->FindClass("cz/cuni/mff/d3s/distrace/Utils");
            //jmethodID methodLoadClass = env->GetStaticMethodID(byteLoader,"forceLoad","(Ljava/lang/String;)Z");


            //auto bytes_for_java = env->NewByteArray(class_data_len);
            //env->SetByteArrayRegion(bytes_for_java, 0, class_data_len, (jbyte*) class_data);



            //std::string nameDots = toNameWithDots(name);
            //std::cout << nameDots << "  nname;;  ";
            //jclass definedClazz = env->DefineClass(nameDots.c_str(), loader, (jbyte*)class_data, class_data_len);


            //std::cout << "THis was success";



            //jclass classClazz = env->FindClass("java/lang/Class");

            //jstring name_for_java = env->NewStringUTF(name);
            //jmethodID forNameMethod = env->GetMethodID(classClazz, "forName", "(Ljava/lang/String;)Ljava/lang/Class;");

            // Load all dependencies for the class
            //env->CallStaticObjectMethod(classClazz, forNameMethod, name_for_java);



           // jstring name_for_java = env->NewStringUTF(name);
           // auto should_continue = env->CallStaticBooleanMethod(byteLoader, methodLoadClass, name_for_java);
            return true;
        }


        /**
        *  The list of class loaders for which we don't want to instrument classes loaded by these class loaders
        */
        std::vector<std::string> ignoredLoaders =  {
                // bootstrap classloader, it loads system classes which we usually don't want to instrument
                "@Bootstrap",
                // our helper classloader used to create TypeDescriptions for classes
                "cz.cuni.mff.d3s.distrace.utils.ClassCreator",
                "sun.reflect.DelegatingClassLoader"
                // classloader created because of mechanism called "inflatation", it is created synthetically and loads synthetical classes
                // and we do not want to create type Descriptions for these internal java classes
                // see http://stackoverflow.com/questions/6505274/what-for-sun-jvm-creates-instances-of-sun-reflect-delegatingclassloader-at-runti
        };

        bool isIgnoredClassLoader(std::string cl){
            return std::find(ignoredLoaders.begin(), ignoredLoaders.end(), cl) != ignoredLoaders.end();
        }

        std::string getClassName(JNIEnv *env, jclass klazz) {
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = env->GetObjectClass(klazz);
            // Find the getName() method in the class object
            jmethodID methodId = env->GetMethodID(clsClazz, "getName", "()Ljava/lang/String;");
            jstring className = (jstring) env->CallObjectMethod(klazz, methodId);
            // Now get the c string from the java jstring object
            const char *str = env->GetStringUTFChars(className, NULL);
            return str;
        }

        jobject getClassLoaderForClass(JNIEnv *env, jclass klazz){
            // Get the class object's class descriptor (jclass inherits from jobject)
            jclass clsClazz = env->GetObjectClass(klazz);
            // Find the getClassLoader() method in the class object
            jmethodID methodId = env->GetMethodID(clsClazz, "getClassLoader", "()Ljava/lang/ClassLoader;");
            return (jobject) env->CallObjectMethod(klazz, methodId);
        }

        std::string getObjectClassName(JNIEnv *env, jobject instance) {
            // Get the class of the object
            jclass cls = env->GetObjectClass(instance);
            return getClassName(env, cls);
        }

        std::string getClassLoaderName(JNIEnv *env, jobject loader) {
            if (loader == NULL) {
                return "@Bootstrap";
                // we put @ sign before it to prevent collision with class
                // which could be potentially named Bootstrap in an empty package. Packages can't start with @
                // so this will keep us safe
            } else {
                return getObjectClassName(env, loader);
            }
        }

    }
}

