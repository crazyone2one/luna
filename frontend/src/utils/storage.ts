export const get = <T>(key: string): T => {
    const json = localStorage.getItem(key)
    try {
        return JSON.parse(json as string)
    } catch {
        return json as any
    }
}

export const set = (key: string, value: unknown): void => {
    localStorage.setItem(key, JSON.stringify(value))
}

export const remove = (key: string): void => {
    localStorage.removeItem(key)
}