/**
 * 本地存储工具
 */

// 缓存数据结构
interface CacheData<T> {
  data: T
  timestamp: number
  expireTime: number // 过期时间（毫秒）
}

export const storage = {
  /**
   * 设置localStorage
   */
  set(key: string, value: any) {
    try {
      const data = JSON.stringify(value)
      localStorage.setItem(key, data)
    } catch (error) {
      console.error('存储数据失败:', error)
    }
  },

  /**
   * 获取localStorage
   */
  get<T>(key: string): T | null {
    try {
      const data = localStorage.getItem(key)
      if (data) {
        return JSON.parse(data) as T
      }
      return null
    } catch (error) {
      console.error('读取数据失败:', error)
      return null
    }
  },

  /**
   * 删除localStorage
   */
  remove(key: string) {
    localStorage.removeItem(key)
  },

  /**
   * 清空localStorage
   */
  clear() {
    localStorage.clear()
  },

  /**
   * 设置带过期时间的缓存
   * @param key 缓存键
   * @param value 缓存值
   * @param expireTime 过期时间（毫秒），默认24小时
   */
  setCache<T>(key: string, value: T, expireTime: number = 24 * 60 * 60 * 1000) {
    try {
      const cacheData: CacheData<T> = {
        data: value,
        timestamp: Date.now(),
        expireTime
      }
      const data = JSON.stringify(cacheData)
      localStorage.setItem(key, data)
    } catch (error) {
      console.error('设置缓存失败:', error)
    }
  },

  /**
   * 获取缓存（自动检查过期）
   * @param key 缓存键
   * @returns 缓存值或null（未找到或已过期）
   */
  getCache<T>(key: string): T | null {
    try {
      const data = localStorage.getItem(key)
      if (!data) {
        return null
      }

      const cacheData: CacheData<T> = JSON.parse(data)
      const now = Date.now()
      
      // 检查是否过期
      if (now - cacheData.timestamp > cacheData.expireTime) {
        // 过期，删除缓存
        localStorage.removeItem(key)
        return null
      }

      return cacheData.data
    } catch (error) {
      console.error('获取缓存失败:', error)
      return null
    }
  },

  /**
   * 检查缓存是否存在且未过期
   */
  hasCache(key: string): boolean {
    return this.getCache(key) !== null
  },

  /**
   * 获取缓存的剩余有效时间（毫秒）
   */
  getCacheRemainingTime(key: string): number {
    try {
      const data = localStorage.getItem(key)
      if (!data) {
        return 0
      }

      const cacheData: CacheData<any> = JSON.parse(data)
      const now = Date.now()
      const elapsed = now - cacheData.timestamp
      const remaining = cacheData.expireTime - elapsed

      return remaining > 0 ? remaining : 0
    } catch (error) {
      return 0
    }
  }
}