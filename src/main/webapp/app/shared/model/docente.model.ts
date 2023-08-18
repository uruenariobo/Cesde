export interface IDocente {
  id?: number;
  nombre?: string;
  documento?: string | null;
  correo?: string | null;
}

export const defaultValue: Readonly<IDocente> = {};
