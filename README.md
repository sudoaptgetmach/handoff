## 1. Endpoints de Eventos (Públicos)

Gerencia a visualização de eventos e escalas.

### Listar Eventos Visíveis

Retorna todos os eventos que estão marcados como visíveis para os usuários.

- **URL:** `/events`
- **Método:** `GET`

### Consultar Roster do Evento

Retorna a lista de reservas confirmadas para um evento específico.

- **URL:** `/events/{id}/roster`
- **Método:** `GET`
- **Saída:** `200 OK` - Lista de `BookingResponseDto` (apenas status `CONFIRMADO`).

---

## 2. Endpoints de Reservas (Públicos)

Operações realizadas pelos usuários em suas próprias solicitações.

### Criar Solicitação de Reserva

Registra um novo interesse de um usuário em uma posição de um evento.

- **URL:** `/bookings`
- **Método:** `POST`
- **Entrada:** `CreateBookingDto` (JSON)
- **Saída:** `201 Created` - `BookingResponseDto`

### Cancelar Reserva

Permite que o usuário cancele sua própria solicitação.

- **URL:** `/bookings/{id}/cancel`
- **Método:** `PATCH`
- **Saída:** `204 No Content`.

---

## 3. Administração de Eventos (Admin)

Controle de sincronização e visibilidade de eventos.

### Listar Todos os Eventos

Consulta eventos com filtros administrativos.

- **URL:** `/admin/events`
- **Método:** `GET`
- **Parâmetros:** `eventId` (opcional), `status` (opcional).
- **Saída:** `200 OK` - Lista de `Event`.

### Sincronizar Eventos (VATSIM)

Inicia o proocesso manual de sincronização com a API de Eventos da VATSIM.

- **URL:** `/admin/events/sync`
- **Método:** `GET`
- **Saída:** `200 OK`.

---

## 4. Administração de Reservas (Admin)

Gestão de aprovações e revisões das solicitações.

### Listar Todas as Reservas

- **URL:** `/admin/bookings`
- **Método:** `GET`
- **Parâmetros:** `eventId` (opcional), `status` (opcional).
- **Saída:** `200 OK` - Lista de `BookingResponseDto`.

### Aprovar Reserva

Aprova a reserva e processa cancelamentos de conflitos.

- **URL:** `/admin/bookings/{id}/approve`
- **Método:** `PATCH`
- **Entrada:** `BookingReviewDto` 
- **Saída:** `204 No Content`.

### Rejeitar Reserva

Recusa a solicitação de reserva.

- **URL:** `/admin/bookings/{id}/reject`
- **Método:** `PATCH`
- **Entrada:** `BookingReviewDto`
- **Saída:** `204 No Content`.