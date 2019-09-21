package week4

import scala.collection.mutable.ListBuffer

case class Film( name: String, yearOfRelease: Int, imdbRating: Double)

case class Director( firstName: String,lastName: String,yearOfBirth: Int,films: Seq[Film])

object lab4 extends App {
  val memento = new Film("Memento", 2000, 8.5)
  val darkKnight = new Film("Dark Knight", 2008, 9.0)
  val inception = new Film("Inception", 2010, 8.8)
  val highPlainsDrifter = new Film("High Plains Drifter", 1973, 7.7)
  val outlawJoseyWales = new Film("The Outlaw Josey Wales", 1976, 7.9)
  val unforgiven = new Film("Unforgiven", 1992, 8.3)
  val granTorino = new Film("Gran Torino", 2008, 8.2)
  val invictus = new Film("Invictus", 2009, 7.4)
  val predator = new Film("Predator", 1987, 7.9)
  val dieHard = new Film("Die Hard", 1988, 8.3)
  val huntForRedOctober = new Film("The Hunt for Red October", 1990, 7.6)
  val thomasCrownAffair = new Film("The Thomas Crown Affair", 1999, 6.8)
  val eastwood = new Director("Clint", "Eastwood", 1930,  Seq(highPlainsDrifter, outlawJoseyWales, unforgiven, granTorino, invictus))
  val mcTiernan = new Director("John", "McTiernan", 1951,  Seq(predator, dieHard, huntForRedOctober, thomasCrownAffair))
  val nolan = new Director("Christopher", "Nolan", 1970, Seq(memento, darkKnight, inception))
  val someGuy = new Director("Just", "Some Guy", 1990, Seq())
  val directors = Seq(eastwood, mcTiernan, nolan, someGuy)

  // #1
  def DirectorsWithFilmsMoreThanNumber (numberOfFilms: Int): Seq[String] = directors.filter( d => d.films.size > numberOfFilms).map( director => director.firstName )
//  println(DirectorsWithFilmsMoreThanNumber(2))

  // #2
  def DirectorsBeforeThatYear (year: Int): Seq[String] = directors.filter( d => d.yearOfBirth < year). map( director => director.firstName)
//  println(DirectorsBeforeThatYear(1971))

  // #3
  def DirectorsBeforeThatYearAndWithFilmMoreThanNumber ( year: Int, numberOfFilms: Int): Seq[Director] = directors.filter( d => d.films.size > numberOfFilms).filter( d => d.yearOfBirth < year)
//  println(DirectorsBeforeThatYearAndWithFilmMoreThanNumber(1971,3))

  // #4
  def OrderByAge(ascending: Boolean) : Seq[Director] = directors.sortWith( (d1, d2) => if(ascending) d1.yearOfBirth > d2.yearOfBirth else d1.yearOfBirth < d2.yearOfBirth )
//  println(OrderByAge(true))

  // #5
  def NolanFilms() : Seq[String] = nolan.films.map( film => film.name)
//  println(NolanFilms)

  // #6
  def Cinephile(): Seq[String] = directors.map( director =>  director.films.map(film => film.name )).flatten
//  println(Cinephile())

  // #7
  def VintageMcTiernan(): Film = {
    mcTiernan.films.minBy(film => film.yearOfRelease)
  }
//  println(VintageMcTiernan())

  // #8
  def HighScoreTable(): Seq[Film] = {
    var allFilms = new ListBuffer[Film]
    directors.foreach(director =>  director.films.foreach( film => allFilms+=film  ))
    allFilms.sortWith((a,b) => a.imdbRating > b.imdbRating).toList
  }
//  println(HighScoreTable())

  // #9
  def AverageImdbRating(): Double = {
    var cnt  = 0
    var totalRating = 0.0
    directors.foreach(directors =>{
      directors.films.foreach(film => totalRating= totalRating +film.imdbRating)
      cnt = cnt + directors.films.size
    })
    totalRating/cnt
  }
//  println(AverageImdbRating())

  // #10
  def TonightListening(): Unit = directors.foreach( directors => directors.films.foreach( film => println(s"Tonight only! ${film.name} by ${directors.firstName}")))
//  TonightListening

  // #11
  def FromTheArchives(dir: Director) : Film ={
    directors.find(directors=> directors.firstName == dir.firstName).get.films.minBy( film => film.yearOfRelease )
  }
//  println(FromTheArchives(nolan))
}