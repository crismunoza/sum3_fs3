import { Producto } from './Producto';

export class Ventas {
    producto: Producto;
    usuario: string;

    constructor(producto: Producto, usuario: string) {
        this.producto = producto;
        this.usuario = usuario;
    }
}