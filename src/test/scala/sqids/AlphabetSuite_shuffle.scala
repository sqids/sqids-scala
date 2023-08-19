package sqids

import munit.ScalaCheckSuite
import sqids.options.Alphabet

class AlphabetSuite_shuffle extends ScalaCheckSuite {
  test("default shuffle, checking for randomness") {
    assertEquals(
      Alphabet.default.shuffle.value,
      "fwjBhEY2uczNPDiloxmvISCrytaJO4d71T0W3qnMZbXVHg6eR8sAQ5KkpLUGF9"
    )
  }
  test("numbers in the front, another check for randomness") {
    val i = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val o = "ec38UaynYXvoxSK7RV9uZ1D2HEPw6isrdzAmBNGT5OCJLk0jlFbtqWQ4hIpMgf"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))
  }
  test("swapping front 2 characters") {
    val i1 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val i2 = "1023456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    val o1 = "ec38UaynYXvoxSK7RV9uZ1D2HEPw6isrdzAmBNGT5OCJLk0jlFbtqWQ4hIpMgf"
    val o2 = "xI3RUayk1MSolQK7e09zYmFpVXPwHiNrdfBJ6ZAT5uCWbntgcDsEqjv4hLG28O"
    Alphabet(i1).foreach(a => assertEquals(a.shuffle.value, o1))
    Alphabet(i2).foreach(a => assertEquals(a.shuffle.value, o2))
  }
  test("swapping last 2 characters") {
    val i1 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val i2 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXZY"

    val o1 = "ec38UaynYXvoxSK7RV9uZ1D2HEPw6isrdzAmBNGT5OCJLk0jlFbtqWQ4hIpMgf"
    val o2 = "x038UaykZMSolIK7RzcbYmFpgXEPHiNr1d2VfGAT5uJWQetjvDswqn94hLC6BO"
    Alphabet(i1).foreach(a => assertEquals(a.shuffle.value, o1))
    Alphabet(i2).foreach(a => assertEquals(a.shuffle.value, o2))
  }
  test("short alphabet") {
    val i = "0123456789"
    val o = "4086517392"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))
  }
  test("really short alphabet") {
    val i = "12345"
    val o = "24135"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))
  }
  test("lowercase alphabet") {
    val i = "abcdefghijklmnopqrstuvwxyz"
    val o = "lbfziqvscptmyxrekguohwjand"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))
  }
  test("uppercase alphabet") {
    val i = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val o = "ZXBNSIJQEDMCTKOHVWFYUPLRGA"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))

  }
  test("bars") {
    val i = "▁▂▃▄▅▆▇█"
    val o = "▂▇▄▅▆▃▁█"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))

  }
  test("bars with numbers") {
    val i = "▁▂▃▄▅▆▇█0123456789"
    val o = "14▅▂▇320▆75▄█96▃8▁"
    Alphabet(i).foreach(a => assertEquals(a.shuffle.value, o))
  }
}
