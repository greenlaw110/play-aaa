package play.modules.aaa.utils;

import play.jobs.Job;
import play.modules.aaa.IAuthorizeable;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRight;

public abstract class AuthorizeableAction<T> extends Job<T> implements IAuthorizeable {

   @Override
   public IRight getRequiredRight() {
      return AnnotationHelper.getRequiredRight(getClass());
   }

   @Override
   public IPrivilege getRequiredPrivilege() {
      return AnnotationHelper.getRequiredPrivilege(getClass());
   }
   
   @Override
   public final void doJob() throws Exception {
      //TODO check permission
      doIt();
   }
   
   @Override
   public final T doJobWithResult() throws Exception {
      // TODO check permission
      return doItWithResult();
   }
   
   /**
    * Here you do your real job
    * @throws Exception
    */
   protected void doIt() throws Exception {
   }
   
   /**
    * Here you do your real job with a result
    * @throws Exception
    */
   protected T doItWithResult() throws Exception {
      doIt();
      return null;
   }
   
}
