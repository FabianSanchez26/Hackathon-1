# Integración del correo con decisiones

Ya existen AsyncConfig, RealityReport y RealityMailService. El servicio usa el
destinatario y los datos de una captura inmutable del estado posterior a la decisión.
No envía correos desde el controlador ni desde DecisionService.

## Contrato propuesto para el integrante 3

Acordar antes de crear el evento definitivo:

- DecisionCommittedEvent contiene RealityReport report y boolean simulateMailFailure.
- Construir RealityReport con todos sus campos, incluidos el ID generado de la decisión,
  email y nombre del dueño, estadísticas posteriores a la decisión y texto original.
- Publicar el evento dentro de la transacción que guarda la decisión y la partida.
- No publicar eventos para ENTRADA_CORRUPTA.
- simulateMailFailure es true solo cuando X-Bandersnatch-Simulate vale MAIL_FAILURE.

## Pendiente del integrante 1 al integrar Decision

Crear RealityLog y su repositorio con la relación bidireccional con Decision.
Crear un listener separado con @Async("branchExecutor"),
@TransactionalEventListener(phase = AFTER_COMMIT) y
@Transactional(propagation = REQUIRES_NEW).

El listener cambia a PROCESANDO, llama a RealityMailService.send y guarda SENT /
ESTABILIZADA o captura el fallo y guarda FAILED / ERROR, con el error y los tiempos
correspondientes. El asunto se obtiene con RealityMailService.subject incluso si
el envío falla. También imprime el BRANCH-LOG requerido.

## Verificación y contrato usado

Las pruebas del correo usan Mockito: no contactan un SMTP real.
No sustituyen los cinco tests obligatorios de DecisionService ni la estrella 5.

El asunto y destinatario siguen los autotests ejecutables incluidos: correo al dueño
y asunto [TUCKERSOFT] <branchType> en <playerTag> | Impacto <impactLevel>.
Las notas v1.3 del README contradicen esos tests; confirmar el contrato con el TA
antes de la entrega final.
