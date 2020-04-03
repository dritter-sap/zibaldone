package data.utils;

public class VersionMismatchException extends NestedException
{
    /*----------------------------------------------------------------------*\
                         Private Static Variables
    \*----------------------------------------------------------------------*/

  /**
   * See JDK 1.5 version of java.io.Serializable
   */
  private static final long serialVersionUID = 1L;

    /*----------------------------------------------------------------------*\
                           Private Instance Data
    \*----------------------------------------------------------------------*/

  private String expectedVersion = null;
  private String foundVersion    = null;

    /*----------------------------------------------------------------------*\
                               Constructors
    \*----------------------------------------------------------------------*/

  /**
   * Constructs an exception containing an error message, but no
   * nested exception.
   *
   * @param message         the message to associate with this exception
   * @param expectedVersion string representing the expected version
   * @param foundVersion    string representing the found version
   *
   * @see #getExpectedVersion
   * @see #getFoundVersion
   */
  public VersionMismatchException (String message,
                                   String expectedVersion,
                                   String foundVersion)
  {
    super (message);
    this.expectedVersion = expectedVersion;
    this.foundVersion = foundVersion;
  }

  /**
   * Constructs an exception containing another exception and a message.
   *
   * @param message         the message to associate with this exception
   * @param expectedVersion string representing the expected version
   * @param foundVersion    string representing the found version
   * @param exception       the exception to contain
   *
   * @see #getExpectedVersion
   * @see #getFoundVersion
   */
  public VersionMismatchException (String    message,
                                   Throwable exception,
                                   String    expectedVersion,
                                   String    foundVersion)
  {
    super (message, exception);
    this.expectedVersion = expectedVersion;
    this.foundVersion = foundVersion;
  }

  public VersionMismatchException (String bundleName,
                                   String messageKey,
                                   String defaultMsg,
                                   String expectedVersion,
                                   String foundVersion)
  {
    super (bundleName, messageKey, defaultMsg);
    this.expectedVersion = expectedVersion;
    this.foundVersion = foundVersion;
  }

  public VersionMismatchException (String   bundleName,
                                   String   messageKey,
                                   String   defaultMsg,
                                   Object[] msgParams,
                                   String   expectedVersion,
                                   String   foundVersion)
  {
    super (bundleName, messageKey, defaultMsg, msgParams);
    this.expectedVersion = expectedVersion;
    this.foundVersion = foundVersion;
  }

  public VersionMismatchException (String    bundleName,
                                   String    messageKey,
                                   String    defaultMsg,
                                   Throwable exception,
                                   String    expectedVersion,
                                   String    foundVersion)
  {
    this (bundleName,
        messageKey,
        defaultMsg,
        null,
        exception,
        expectedVersion,
        foundVersion);
  }

  public VersionMismatchException (String    bundleName,
                                   String    messageKey,
                                   String    defaultMsg,
                                   Object[]  msgParams,
                                   Throwable exception,
                                   String    expectedVersion,
                                   String    foundVersion)
  {
    super (bundleName, messageKey, defaultMsg, msgParams, exception);
    this.expectedVersion = expectedVersion;
    this.foundVersion = foundVersion;
  }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

  /**
   * Get the expected version string from this exception.
   *
   * @return the expected version string
   */
  public String getExpectedVersion()
  {
    return expectedVersion;
  }

  /**
   * Get the found version string from this exception.
   *
   * @return the found version string
   */
  public String getFoundVersion()
  {
    return foundVersion;
  }
}
