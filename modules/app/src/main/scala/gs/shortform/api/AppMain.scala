package gs.shortform.api

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp

object ApiMain extends IOApp:

  /** Run ShortForm.
    *
    * @param args
    *   Command line arguments.
    * @return
    *   0 if successful, an integer error code otherwise.
    */
  def run(args: List[String]): IO[ExitCode] =
    IO(println("Running ShortForm API")).as(ExitCode.Success)

end ApiMain
