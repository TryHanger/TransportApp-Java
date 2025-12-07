const api = {
  vehicles: '/api/vehicles',
  drivers: '/api/drivers',
  routes: '/api/routes'
};

function el(tag, props = {}, ...children) {
  const e = document.createElement(tag);
  Object.assign(e, props);
  children.flat().forEach(c => {
    if (typeof c === 'string') e.appendChild(document.createTextNode(c));
    else if (c) e.appendChild(c);
  });
  return e;
}

async function request(url, options = {}){
  try {
    const res = await fetch(url, Object.assign({headers:{'Content-Type':'application/json'}}, options));
    if (!res.ok) {
      const text = await res.text().catch(()=>res.statusText||'');
      throw new Error(res.status + (text ? (": "+text) : ""));
    }
    if (res.status === 204) return null;
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    return null;
  } catch (err) {
    // пробрасываем дальше — вызывающий код покажет ошибку
    throw err;
  }
}

function navTo(hash) {
  location.hash = hash;
}

async function VehiclesView(){
  const card = el('div',{className:'card'});
  card.appendChild(el('h2',{},'Транспорт'));

  // form with select for type and a filter above the list
  const types = ['Все','Грузовая','Легковая','Автобус','Микроавтобус','Спецтехника','Прицеп','Электромобиль'];
  const form = el('form',{className:'inline'},
    el('input',{placeholder:'Модель',id:'v-model'}),
    (function(){
      const sel = el('select',{id:'v-type'});
      sel.appendChild(el('option',{value:''},'— Тип —'));
      types.slice(1).forEach(t=> sel.appendChild(el('option',{value:t},t)));
      return sel;
    })(),
    el('input',{placeholder:'Вместимость',id:'v-capacity',type:'number'}),
    el('button',{type:'button',onclick: async ()=>{
      try{
        const model = document.getElementById('v-model').value.trim();
        const type = document.getElementById('v-type').value.trim();
        const capacity = parseFloat(document.getElementById('v-capacity').value||0);
        if (!model) return alert('Поле "Модель" обязательно');
        if (!type) return alert('Поле "Тип" обязательно');
        if (!(capacity>0)) return alert('Поле "Вместимость" обязательно и должно быть > 0');
        const payload={model,type,capacity};
        await request(api.vehicles,{method:'POST',body:JSON.stringify(payload)});
        render();
      } catch(e){ alert('Ошибка при добавлении: '+e.message); }
    }},'Добавить')
  );
  card.appendChild(form);

  let data;
  try {
    data = await request(api.vehicles);
  } catch (e) {
    card.appendChild(el('div',{},'Ошибка загрузки: '+e.message));
    return card;
  }

  if (!data || data.length === 0) {
    card.appendChild(el('div',{},'Нет записей. Попробуйте добавить новую запись.'));
    return card;
  }

  const list = el('div',{className:'list'});
  for (const v of data){
    const item = el('div',{className:'item'},
      el('div',{},`${v.id} — ${v.model} (${v.type}) — ${v.capacity}`),
      el('div',{className:'controls'},
  el('button',{className:'ghost',onclick:()=>navTo('#/vehicles/'+v.id)},'Редактировать'),
        el('button',{onclick:async ()=>{try{await request(api.vehicles+'/'+v.id,{method:'DELETE'});render();}catch(e){alert('Ошибка: '+e.message);}}},'Удалить')
      )
    );
    list.appendChild(item);
  }
  card.appendChild(list);
  return card;
}

async function DriversView(){
  const card = el('div',{className:'card'});
  card.appendChild(el('h2',{},'Водители'));
  const form = el('form',{className:'inline', style:'position:relative;'},
    el('input',{placeholder:'Имя',id:'d-name'}),
    (function(){
      // custom multi-select dropdown with checkboxes
      const categories = ['A','A1','B','B1','BE','C','C1','CE','C1E','D','D1','DE','D1E','M'];
      const wrapper = el('div',{className:'multi-select', style:'position:relative; display:inline-block;'});
      const btn = el('button',{type:'button',className:'multi-btn', onclick: (e)=>{
        e.preventDefault();
        const panel = wrapper.querySelector('.multi-panel');
        panel.style.display = panel.style.display === 'block' ? 'none' : 'block';
      }}, '— Категория —');
      const panel = el('div',{className:'multi-panel', style:'display:none; position:absolute; zIndex:10; background:#fff; border:1px solid #e6eef9; padding:8px; margin-top:6px; max-height:200px; overflow:auto; box-shadow:0 4px 12px rgba(0,0,0,0.06);'},
        categories.map(c => el('label',{style:'display:block;margin:4px 0;'}, el('input',{type:'checkbox',className:'d-cat-chk',value:c}), ' ', c))
      );
      // close panel when clicking outside
      document.addEventListener('click', (ev)=>{
        if (!wrapper.contains(ev.target)) panel.style.display='none';
      });
      wrapper.appendChild(btn);
      wrapper.appendChild(panel);
      return wrapper;
    })(),
    el('button',{type:'button',onclick: async ()=>{
      try{
        const name = document.getElementById('d-name').value.trim();
        const selected = Array.from(document.querySelectorAll('.d-cat-chk')).filter(i=>i.checked).map(i=>i.value);
        if (!name) return alert('Поле "Имя" обязательно');
        if (!selected || selected.length === 0) return alert('Выберите хотя бы одну категорию');
        await request(api.drivers,{method:'POST',body:JSON.stringify({name,licenseCategory:selected})});
        // clear selections
        document.getElementById('d-name').value='';
        document.querySelectorAll('.d-cat-chk').forEach(i=>i.checked=false);
        render();
      } catch(e){ alert('Ошибка: '+e.message); }
    }},'Добавить')
  );
  card.appendChild(form);
  let data;
  try {
    data = await request(api.drivers);
  } catch (e) {
    card.appendChild(el('div',{},'Ошибка загрузки: '+e.message));
    return card;
  }
  if (!data || data.length === 0) {
    card.appendChild(el('div',{},'Нет записей. Добавьте водителя.'));
    return card;
  }
  const list = el('div',{className:'list'});
  for (const d of data){
    const cats = d.licenseCategories ? (Array.isArray(d.licenseCategories) ? d.licenseCategories.join(', ') : String(d.licenseCategories)) : (d.licenseCategory ? d.licenseCategory : '—');
    const item = el('div',{className:'item'},
      el('div',{},`${d.id} — ${d.name} (${cats})`),
      el('div',{className:'controls'},
  el('button',{className:'ghost',onclick:()=>navTo('#/drivers/'+d.id)},'Редактировать'),
        el('button',{onclick:async ()=>{try{await request(api.drivers+'/'+d.id,{method:'DELETE'});render();}catch(e){alert('Ошибка: '+e.message);}}},'Удалить')
      )
    );
    list.appendChild(item);
  }
  card.appendChild(list);
  return card;
}

async function RoutesView(){
  const card = el('div',{className:'card'});
  card.appendChild(el('h2',{},'Маршруты'));
  let routes, vehicles, drivers;
  try {
    [routes, vehicles, drivers] = await Promise.all([request(api.routes), request(api.vehicles), request(api.drivers)]);
  } catch (e) {
    card.appendChild(el('div',{},'Ошибка загрузки: '+e.message));
    return card;
  }

  const form = el('form',{className:'inline'},
    el('input',{placeholder:'Откуда',id:'r-from'}),
    el('input',{placeholder:'Куда',id:'r-to'}),
    el('input',{placeholder:'Км',type:'number',id:'r-dist'}),
    (function(){
      const sel = el('select',{id:'r-vehicle'});
      sel.appendChild(el('option',{value:''},'— Транспорт —'));
      for(const v of (vehicles||[])){
        const cap = v.capacity == null ? '—' : v.capacity;
        sel.appendChild(el('option',{value:v.id},`${v.model} (вместимость: ${cap})`));
      }
      return sel;
    })(),
    (function(){
      const sel = el('select',{id:'r-driver'});
      sel.appendChild(el('option',{value:''},'— Водитель —'));
      for(const d of (drivers||[])){
        const cats = d.licenseCategories ? (Array.isArray(d.licenseCategories) ? d.licenseCategories.join(', ') : String(d.licenseCategories)) : (d.licenseCategory ? d.licenseCategory : '—');
        sel.appendChild(el('option',{value:d.id},`${d.name} (${cats})`));
      }
      return sel;
    })(),
    el('button',{type:'button',onclick: async ()=>{
      try{
        const start = document.getElementById('r-from').value.trim();
        const end = document.getElementById('r-to').value.trim();
        const distance = parseFloat(document.getElementById('r-dist').value||0);
        const vehicleId = document.getElementById('r-vehicle').value;
        const driverId = document.getElementById('r-driver').value;
        if (!start) return alert('Поле "Откуда" обязательно');
        if (!end) return alert('Поле "Куда" обязательно');
        if (!(distance>0)) return alert('Поле "Км" обязательно и должно быть > 0');
        if (!vehicleId) return alert('Выберите транспорт');
        if (!driverId) return alert('Выберите водителя');
        const payload={startPoint:start,endPoint:end,distance,vehicleId:vehicleId,driverId:driverId};
        await request(api.routes,{method:'POST',body:JSON.stringify(payload)});
        render();
      } catch(e){ alert('Ошибка при добавлении: '+e.message); }
    }},'Добавить')
  );
  card.appendChild(form);

  if (!routes || routes.length === 0) {
    card.appendChild(el('div',{},'Нет маршрутов. Добавьте маршрут.'));
    return card;
  }

  const list = el('div',{className:'list'});
  for (const r of routes){
    const v = (vehicles||[]).find(x=>x.id==r.vehicleId);
    const d = (drivers||[]).find(x=>x.id==r.driverId);
    const item = el('div',{className:'item'},
      el('div',{},`${r.id} — ${r.startPoint} → ${r.endPoint} — ${r.distance} км — ${v?v.model:'—'} / ${d?d.name:'—'}`),
      el('div',{className:'controls'},
  el('button',{className:'ghost',onclick:()=>navTo('#/routes/'+r.id)},'Редактировать'),
        el('button',{onclick:async ()=>{try{await request(api.routes+'/'+r.id,{method:'DELETE'});render();}catch(e){alert('Ошибка: '+e.message);}}},'Удалить')
      )
    );
    list.appendChild(item);
  }
  card.appendChild(list);
  return card;
}

async function render(){
  const mount = document.getElementById('app');
  mount.innerHTML='';
  const hash = location.hash || '#/vehicles';
  if (hash.startsWith('#/vehicles/')) {
    const id = hash.split('/')[2];
    const v = await request(api.vehicles+'/'+id);
    const card = el('div',{className:'card'});
    card.appendChild(el('h2',{},`Редактировать транспорт #${id}`));
    const form = el('form',{className:'inline'},
      el('input',{id:'edit-v-model', value: v.model}),
      (function(){const sel=el('select',{id:'edit-v-type'});const types=['Грузовая','Легковая','Автобус','Микроавтобус','Спецтехника','Прицеп','Электромобиль'];sel.appendChild(el('option',{value:''},'— Тип —'));types.forEach(t=>sel.appendChild(el('option',{value:t},t)));sel.value=v.type||'';return sel;})(),
      el('input',{id:'edit-v-cap',type:'number',value:v.capacity}),
      el('button',{type:'button',onclick:async ()=>{
        try{
          const model=document.getElementById('edit-v-model').value.trim();
          const type=document.getElementById('edit-v-type').value.trim();
          const capacity=parseFloat(document.getElementById('edit-v-cap').value||0);
          if(!model) return alert('Поле "Модель" обязательно');
          if(!type) return alert('Поле "Тип" обязательно');
          if(!(capacity>0)) return alert('Поле "Вместимость" обязательно и > 0');
          const payload={model,type,capacity};
          const res=await request(api.vehicles+'/'+id,{method:'PUT',body:JSON.stringify(payload)});
          navTo('#/vehicles');
        }catch(e){alert('Ошибка: '+e.message)}
      }},'Сохранить'),
      el('button',{type:'button',onclick:async ()=>{if(confirm('Удалить транспорт?')){try{await request(api.vehicles+'/'+id,{method:'DELETE'});navTo('#/vehicles');}catch(e){alert('Ошибка: '+e.message)}}}},'Удалить'),
      el('button',{onclick:()=>navTo('#/vehicles')},'Отмена')
    );
    card.appendChild(form);
    mount.appendChild(card);
    return;
  }
  if (hash === '#/vehicles') mount.appendChild(await VehiclesView());
  else if (hash.startsWith('#/drivers/')) {
    const id = hash.split('/')[2];
    const d = await request(api.drivers + '/' + id);
    const card = el('div',{className:'card'});
    card.appendChild(el('h2',{},`Редактировать водителя #${id}`));
    const categories = ['A','A1','B','B1','BE','C','C1','CE','C1E','D','D1','DE','D1E','M'];
    const form = el('form',{className:'inline', style:'position:relative;'},
      el('input',{id:'edit-d-name', value: d.name}),
      (function(){
        const wrapper = el('div',{style:'position:relative; display:inline-block;'});
        const btn = el('button',{type:'button',onclick:(e)=>{e.preventDefault();const p=wrapper.querySelector('.edit-multi-panel');p.style.display=p.style.display==='block'?'none':'block';}}, 'Категории');
        const panel = el('div',{className:'edit-multi-panel', style:'display:none; position:absolute; zIndex:10; background:#fff; border:1px solid #e6eef9; padding:8px; margin-top:6px; max-height:200px; overflow:auto; box-shadow:0 4px 12px rgba(0,0,0,0.06);'},
          categories.map(c => {
            const checked = d.licenseCategories && d.licenseCategories.includes(c);
            return el('label',{style:'display:block;margin:4px 0;'}, el('input',{type:'checkbox',className:'edit-d-cat-chk',value:c,checked:checked}), ' ', c);
          })
        );
        document.addEventListener('click',(ev)=>{ if(!wrapper.contains(ev.target)) panel.style.display='none'; });
        wrapper.appendChild(btn); wrapper.appendChild(panel); return wrapper;
      })(),
      el('button',{type:'button',onclick:async ()=>{
        try{
          const name=document.getElementById('edit-d-name').value.trim();
          const selected=Array.from(document.querySelectorAll('.edit-d-cat-chk')).filter(i=>i.checked).map(i=>i.value);
          if(!name) return alert('Поле "Имя" обязательно');
          if(!selected.length) return alert('Выберите хотя бы одну категорию');
          await request(api.drivers+'/'+id,{method:'PUT',body:JSON.stringify({name,licenseCategories:selected})});
          navTo('#/drivers');
        }catch(e){alert('Ошибка: '+e.message)}
      }},'Сохранить'),
      el('button',{type:'button',onclick:async ()=>{if(confirm('Удалить водителя?')){try{await request(api.drivers+'/'+id,{method:'DELETE'});navTo('#/drivers');}catch(e){alert('Ошибка: '+e.message)}}}},'Удалить'),
      el('button',{onclick:()=>navTo('#/drivers')},'Отмена')
    );
    card.appendChild(form);
    mount.appendChild(card);
    return;
  }
  else if (hash === '#/drivers') mount.appendChild(await DriversView());
  else if (hash.startsWith('#/routes/')){
    const id = hash.split('/')[2];
    const r = await request(api.routes+'/'+id);
    const [vehicles, drivers] = await Promise.all([request(api.vehicles), request(api.drivers)]);
    const card = el('div',{className:'card'});
    card.appendChild(el('h2',{},`Редактировать маршрут #${id}`));
    const form = el('form',{className:'inline'},
      el('input',{id:'edit-r-from', value: r.startPoint}),
      el('input',{id:'edit-r-to', value: r.endPoint}),
      el('input',{id:'edit-r-dist', type:'number', value: r.distance}),
      (function(){ const sel=el('select',{id:'edit-r-vehicle'}); sel.appendChild(el('option',{value:''},'— Транспорт —')); for(const v of (vehicles||[])){ sel.appendChild(el('option',{value:v.id},`${v.model} (вместимость: ${v.capacity||'—'})`)); } sel.value = r.vehicleId||''; return sel; })(),
      (function(){ const sel=el('select',{id:'edit-r-driver'}); sel.appendChild(el('option',{value:''},'— Водитель —')); for(const d of (drivers||[])){ const cats = d.licenseCategories ? (Array.isArray(d.licenseCategories)?d.licenseCategories.join(', '):String(d.licenseCategories)) : (d.licenseCategory?d.licenseCategory:'—'); sel.appendChild(el('option',{value:d.id},`${d.name} (${cats})`)); } sel.value = r.driverId||''; return sel; })(),
      el('button',{type:'button',onclick:async ()=>{
        try{
          const start=document.getElementById('edit-r-from').value.trim();
          const end=document.getElementById('edit-r-to').value.trim();
          const distance=parseFloat(document.getElementById('edit-r-dist').value||0);
          const vehicleId=document.getElementById('edit-r-vehicle').value;
          const driverId=document.getElementById('edit-r-driver').value;
          if(!start) return alert('Поле "Откуда" обязательно');
          if(!end) return alert('Поле "Куда" обязательно');
          if(!(distance>0)) return alert('Поле "Км" обязательно и > 0');
          if(!vehicleId) return alert('Выберите транспорт');
          if(!driverId) return alert('Выберите водителя');
          await request(api.routes+'/'+id,{method:'PUT',body:JSON.stringify({startPoint:start,endPoint:end,distance,vehicleId,driverId})});
          navTo('#/routes');
        }catch(e){alert('Ошибка: '+e.message)}
      }},'Сохранить'),
      el('button',{type:'button',onclick:async ()=>{if(confirm('Удалить маршрут?')){try{await request(api.routes+'/'+id,{method:'DELETE'});navTo('#/routes');}catch(e){alert('Ошибка: '+e.message)}}}},'Удалить'),
      el('button',{onclick:()=>navTo('#/routes')},'Отмена')
    );
    card.appendChild(form);
    mount.appendChild(card);
    return;
  }
  else if (hash === '#/routes') mount.appendChild(await RoutesView());
  else mount.appendChild(el('div',{},'Неизвестная страница'));
}

window.addEventListener('hashchange', render);
window.addEventListener('load', render);
