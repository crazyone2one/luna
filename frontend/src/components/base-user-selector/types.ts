export interface IUserSelectorOption {
    id: string;
    name: string;
    email: string;
    disabled?: boolean;
    [key: string]: string | number | boolean | undefined;
}
