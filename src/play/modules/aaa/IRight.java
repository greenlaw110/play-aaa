package play.modules.aaa;

/**
 * A <code>IRight</code> commutes between a {@link IRole} and a {@link IAthorizeable}. 
 * If a {@link IAuthorizeable} (action for example) require a certain <code>IRight</code>
 * then all {@link IRole roles} of the current {@ IAccount principal} will be enumerated
 * to see if any one of them has the same <code>IRight</code>
 * 
 * @author greenlaw110
 * @version 1.0 21/12/2010
 */
public interface IRight {
   /**
    * Return the name of this right. Two rights are considered equals if the name
    * of the two are equal
    * 
    * @return name of the right
    */
   String getName();
   
   /**
    * Factory method to create an new right instance with given name 
    * 
    * @param name
    * @return
    */
   IRight create(String name);
}
