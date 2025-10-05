/**
 * 本地存储工具
 */

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
  }
}