package org.lion.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the table column annotation. this annotation indicate the table column's
 * properties
 * 
 * @author onlynight
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Column {

	/**
	 * appoint the column name
	 * 
	 * @return
	 */
	public String value() default "";

	/**
	 * appoint is the column is unique
	 * 
	 * @return
	 */
	public boolean unique() default false;

	/**
	 * appoint is the column is not null
	 * 
	 * @return
	 */
	public boolean notnull() default false;

	/**
	 * appoint the column's foreign key
	 * 
	 * @return
	 */
	public ForeignKey foreignkey() default @ForeignKey(srcClass = Class.class, column = "");

	/**
	 * appoint the column check constraint
	 * 
	 * @return
	 */
	public String check() default "";

	/**
	 * appoint the column default value
	 * 
	 * @return
	 */
	public String default_value() default "";
}
