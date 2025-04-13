package org.bob.siungongsi.api.service;

import org.bob.siungongsi.common.util.RedisUtils;
import org.springframework.stereotype.Service;

@Service
public class StockCacheService {
  private final RedisUtils redisUtils;
  private static final String PREFIX = "stock:";

  public StockCacheService(RedisUtils redisUtils) {
    this.redisUtils = redisUtils;
  }

  public void cacheStockPrice(String stockCode, Double value) {
    redisUtils.set(PREFIX + stockCode, value, 300000L);
  }

  public Object getCachedStockPrice(String stockCode) {
    return redisUtils.get(PREFIX + stockCode);
  }

  public boolean hasCachedStock(String stockCode) {
    return redisUtils.hasKey(PREFIX + stockCode);
  }
}
