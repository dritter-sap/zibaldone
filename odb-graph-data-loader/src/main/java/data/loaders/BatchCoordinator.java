package data.loaders;

import com.orientechnologies.orient.core.db.ODatabaseSession;

public class BatchCoordinator {
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
      session.begin();
    }
  }
}
