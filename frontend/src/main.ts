import {bootstrapApplication} from '@angular/platform-browser';
import {provideHttpClient} from '@angular/common/http';
import {Component,inject,signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {forkJoin,firstValueFrom} from 'rxjs';
import {ApiService} from './api.service';
import {Product,Order,Status,Dashboard} from './models';
@Component({selector:'app-root',standalone:true,imports:[CommonModule],templateUrl:'./app.html'})
class App {
 api=inject(ApiService);view=signal('overview');products=signal<Product[]>([]);orders=signal<Order[]>([]);
 stats=signal<Dashboard>({products:0,orders:0,lowStock:0,bookedValue:0});
 loading=signal(true);busy=signal(false);notice=signal('');error=signal('');search=signal('');category=signal('All');
 cart=signal<Record<number,number>>({});retailer=signal('Maple Street Market');selected=signal<Order|null>(null);
 pending:{fingerprint:string;key:string}|null=null;
 constructor(){void this.load();}
 async load(){try{const r=await firstValueFrom(forkJoin({products:this.api.products(),orders:this.api.orders(),stats:this.api.dashboard()}));this.products.set(r.products);this.orders.set(r.orders);this.stats.set(r.stats);const id=this.selected()?.id;if(id)this.selected.set(r.orders.find(o=>o.id===id)||null);}catch{this.error.set('Cannot reach the API. Start the Java backend, then refresh.');}finally{this.loading.set(false);}}
 filtered(){return this.products().filter(p=>(this.category()==='All'||p.category===this.category())&&(p.name+' '+p.sku).toLowerCase().includes(this.search().toLowerCase()));}
 categories(){return ['All',...new Set(this.products().map(p=>p.category))];}
 quantity(p:Product){return this.cart()[p.id]||0;}
 add(p:Product,delta:number){const q=this.quantity(p)+delta;if(q<0||q>Math.min(p.stock,1000))return;const c={...this.cart()};if(q===0)delete c[p.id];else c[p.id]=q;this.cart.set(c);this.pending=null;}
 cartLines(){return this.products().filter(p=>this.quantity(p)>0);}
 total(){return this.cartLines().reduce((s,p)=>s+p.price*this.quantity(p),0);}
 units(){return Object.values(this.cart()).reduce((s,q)=>s+q,0);}
 text(event:Event){return (event.target as HTMLInputElement).value;}
 next(s:Status):Status|null{return s==='PLACED'?'CONFIRMED':s==='CONFIRMED'?'SHIPPED':s==='SHIPPED'?'DELIVERED':null;}
 async action(work:()=>Promise<unknown>,message:string){if(this.busy())return;this.busy.set(true);this.error.set('');this.notice.set('');try{await work();this.notice.set(message);await this.load();}catch(e:any){this.error.set(e?.error?.message||'Request failed. Please try again.');}finally{this.busy.set(false);}}
 async place(){if(!this.units()||!this.retailer().trim()||this.busy())return;const items=this.cartLines().map(p=>({productId:p.id,quantity:this.quantity(p)}));const fingerprint=JSON.stringify([this.retailer().trim(),items]);if(this.pending?.fingerprint!==fingerprint)this.pending={fingerprint,key:crypto.randomUUID()};const key=this.pending!.key;
 await this.action(async()=>{const o=await firstValueFrom(this.api.create(this.retailer().trim(),items,key));this.cart.set({});this.pending=null;this.selected.set(o);this.view.set('orders');},'Order placed. Inventory has been reserved.');}
 async restock(p:Product){await this.action(()=>firstValueFrom(this.api.restock(p.id,20)),'Added 20 units to '+p.name+'.');}
 async change(o:Order,s:Status){await this.action(()=>firstValueFrom(this.api.status(o.id,s)),s==='CANCELLED'?'Order cancelled and stock returned.':'Order updated to '+s.toLowerCase()+'.');}
}
bootstrapApplication(App,{providers:[provideHttpClient()]}).catch(console.error);
