package cats.framework.model

import cats.effect.{IO, Ref, Sync}

trait AtomicRef:

  //genera un riferimento atomico
  def refCreate[A](a: A): IO[Ref[IO, A]] = Ref[IO].of(a)

  //restituisce il valore di una ref
  def refGet[F[_] : Sync, A](ref: Ref[F, A])(implicit sync: Sync[F]): F[A] = ref.get

  //aggiorna e ritorna il nuovo valore
  def refUpdateAndGetNewVal[F[_] : Sync, A](ref: Ref[F, A])(f: A => A)(implicit sync: Sync[F]): F[A] = ref.updateAndGet(f)

  //ritenta fino a che non esegue l'update con successo
  def refModifyUntilSucceed[F[_] : Sync, A, B](ref: Ref[F, A])(f: A => (A, B))(implicit sync: Sync[F]): F[B] = ref.modify(f)


/*private object AtomicRef:
  def apply(): AtomicRef =
    AtomicRefImpl()
  private class AtomicRefImpl extends AtomicRef*/



