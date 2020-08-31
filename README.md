# toylang-interpreter
### Project
The following defines a simple language, in which a program consists of assignments and each variable is assumed to be of the integer type. For the sake of simplicity, only operators that give integer values are included. Write an interpreter for the language in a language of your choice. Your interpreter should be able to do the following for a given program: (1) detect syntax errors; (2) report uninitialized variables; and (3) perform the assignments if there is no error and print out the values of all the variables after all the assignments are done.
An interpreter for the Toy Language.

### How To Use
```java -jar <~/toylang>.jar -vars <optional path to a .toy file>```

### Context Free Grammar (CFG)
The language's Context Free Gramar is as following:

<b>Program:</b> Assignment*

<b>Assignment:</b> Identifier = Exp;

<b>Exp:</b> Exp + Term | Exp - Term | Term

<b>Term:</b> Term * Fact  | Fact

<b>Fact:</b> ( Exp ) | - Fact | + Fact | Literal | Identifier

<b>Identifier:</b> Letter [Letter | Digit]*

<b>Letter:</b> a|...|z|A|...|Z|_

<b>Literal:</b> 0 | NonZeroDigit Digit*
		
<b>NonZeroDigit:</b> 1|...|9

<b>Digit:</b> 0|1|...|9
