package data.loaders;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchCoordinator {
  private static final Logger log = LoggerFactory.getLogger(BatchCoordinator.class);

  private long batchSize;

  public BatchCoordinator(final long batchSize) {
    this.batchSize = batchSize;
  }

  public void begin(final ODatabaseSession session) {
    session.begin();
  }

  public void end(final ODatabaseSession session) {
    session.commit();
  }

  public void iterate(final ODatabaseSession session, final long recordNumber) {
    if (recordNumber % batchSize == 0) {
      session.commit();
      log.debug("Record: " + recordNumber);
      log.debug("Total {}, free {} memory", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory());
      session.begin();
    }
  }
}
