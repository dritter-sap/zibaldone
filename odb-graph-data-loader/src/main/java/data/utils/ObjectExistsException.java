package data.utils;

public class ObjectExistsException extends NestedException
{
    /*----------------------------------------------------------------------*\
                         Private Static Variables
    \*----------------------------------------------------------------------*/

  /**
   * See JDK 1.5 version of java.io.Serializable
   */
  private static final long serialVersionUID = 1L;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

  /**
   * Default constructor, for an exception with no nested exception and
   * no message.
   */
  public ObjectExistsException()
  {
    super();
  }

  /**
   * Constructs an exception containing another exception, but no message
   * of its own.
   *
   * @param exception  the exception to contain
   */
  public ObjectExistsException (Throwable exception)
  {
    super (exception);
  }

  /**
   * Constructs an exception containing an error message, but no
   * nested exception.
   *
   * @param message  the message to associate with this exception
   */
  public ObjectExistsException (String message)
  {
    super (message);
  }

  /**
   * Constructs an exception containing another exception and a message.
   *
   * @param message    the message to associate with this exception
   * @param exception  the exception to contain
   */
  public ObjectExistsException (String message, Throwable exception)
  {
    super (message, exception);
  }

  public ObjectExistsException (String bundleName,
                                String messageKey,
                                String defaultMsg)
  {
    super (bundleName, messageKey, defaultMsg);
  }

  public ObjectExistsException (String   bundleName,
                                String   messageKey,
                                String   defaultMsg,
                                Object[] msgParams)
  {
    super (bundleName, messageKey, defaultMsg, msgParams);
  }

  public ObjectExistsException (String    bundleName,
                                String    messageKey,
                                String    defaultMsg,
                                Throwable exception)
  {
    this (bundleName, messageKey, defaultMsg, null, exception);
  }

  public ObjectExistsException (String    bundleName,
                                String    messageKey,
                                String    defaultMsg,
                                Object[]  msgParams,
                                Throwable exception)
  {
    super (bundleName, messageKey, defaultMsg, msgParams, exception);
  }
}
