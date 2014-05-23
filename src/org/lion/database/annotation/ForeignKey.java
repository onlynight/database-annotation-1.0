package org.lion.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * the annotation will use in the @Column annotation this annotation will
 * appoint the column's foreign key
 * 
 * @author onlynight
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface ForeignKey {

	/**
	 * foreign key connect table class
	 * 
	 * @return
	 */
	public Class<?> srcClass();

	/**
	 * the property will appoint the foreign key column's name from the connect @srcClass
	 * 
	 * @return
	 */
	public String column();

	/**
	 * when the @srcClass table update , then the foreign table will update
	 * 
	 * @return
	 */
	public boolean onDelete() default false;

	@Deprecated
	public boolean onInsert() default false;

	/**
	 * when the @srcClass table update the foreign table will update
	 * 
	 * @return
	 */
	public boolean onUpdate() default false;
}
