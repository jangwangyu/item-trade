package org.example.itemtrade.Util;

import java.util.Collection;
import org.example.itemtrade.dto.SoftDelete;

public class SoftDeleteUtil {
  public static void softDeleteAll(Collection<? extends SoftDelete> entities) {
    entities.forEach(SoftDelete::softDelete);
  }
}
