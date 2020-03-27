package data.loaders;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import data.MemoryUtils;
import me.tongfei.progressbar.ProgressBar;
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

  public void iterate(final ODatabaseSession session, final long recordNumber, final ProgressBar pb) {
    if (recordNumber % batchSize == 0) {
      session.commit();
      // log.debug("Record: " + recordNumber);
      pb.stepBy(batchSize);
      if (recordNumber % (batchSize * 100) == 0) {
        pb.setExtraMessage("Mem(used/max)" + MemoryUtils.usedMemoryInMB() + "/" + MemoryUtils.maxMemoryInMB());
      }
      session.begin();
    }
  }
}
