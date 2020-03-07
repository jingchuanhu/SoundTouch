//
// Created by 王经彪 on 2020/2/27.
//

#ifndef SOUNDTOUCH_CONSTRUCTOR_MAGIC_H
#define SOUNDTOUCH_CONSTRUCTOR_MAGIC_H

// Put this in the declarations for a class to be unassignable.
#define RTC_DISALLOW_ASSIGN(TypeName) \
  TypeName& operator=(const TypeName&) = delete

// A macro to disallow the copy constructor and operator= functions. This should
// be used in the declarations for a class.
#define RTC_DISALLOW_COPY_AND_ASSIGN(TypeName) \
  TypeName(const TypeName&) = delete;          \
  RTC_DISALLOW_ASSIGN(TypeName)

// A macro to disallow all the implicit constructors, namely the default
// constructor, copy constructor and operator= functions.
//
// This should be used in the declarations for a class that wants to prevent
// anyone from instantiating it. This is especially useful for classes
// containing only static methods.
#define RTC_DISALLOW_IMPLICIT_CONSTRUCTORS(TypeName) \
  TypeName() = delete;                               \
  RTC_DISALLOW_COPY_AND_ASSIGN(TypeName)

#endif //SOUNDTOUCH_CONSTRUCTOR_MAGIC_H
