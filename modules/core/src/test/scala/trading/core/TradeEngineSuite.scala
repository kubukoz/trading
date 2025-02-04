package trading.core

import java.time.Instant
import java.util.UUID

import trading.commands.TradeCommand
import trading.core.TradeEngine.fsm
import trading.domain.TradingStatus.*
import trading.domain.*
import trading.state.*

import cats.data.NonEmptyList
import weaver.FunSuite
import weaver.scalacheck.Checkers

object TradeEngineSuite extends FunSuite with Checkers:
  val id  = CommandId(UUID.randomUUID())
  val cid = CorrelationId(UUID.randomUUID())
  val s   = Symbol("EURUSD")
  val ts  = Timestamp(Instant.parse("2021-09-16T14:00:00.00Z"))

  val p1 = Price(1.1987)
  val q1 = Quantity(10)

  val p2 = Price(3.5782)
  val q2 = Quantity(20)

  test("Trade engine fsm") {
    val st1 = fsm.runS(TradeState.empty, TradeCommand.Create(id, cid, s, TradeAction.Ask, p1, q1, "test", ts))
    val ex1 = TradeState(On, Map(s -> Prices(ask = Map(p1 -> q1), bid = Map.empty, p1, p1)))

    val st2 = fsm.runS(st1, TradeCommand.Update(id, cid, s, TradeAction.Ask, p2, q2, "test", ts))
    val ex2 = TradeState(On, Map(s -> Prices(ask = Map(p1 -> q1, p2 -> q2), bid = Map.empty, p2, p1)))

    val st3 = fsm.runS(st2, TradeCommand.Delete(id, cid, s, TradeAction.Ask, p1, "test", ts))
    val ex3 = TradeState(On, Map(s -> Prices(ask = Map(p2 -> q2), bid = Map.empty, p2, p1)))

    val st4 = fsm.runS(st3, TradeCommand.Create(id, cid, s, TradeAction.Bid, p1, q1, "test", ts))
    val ex4 = TradeState(On, Map(s -> Prices(ask = Map(p2 -> q2), bid = Map(p1 -> q1), p2, p1)))

    NonEmptyList
      .of(
        expect.same(st1, ex1),
        expect.same(st2, ex2),
        expect.same(st3, ex3),
        expect.same(st4, ex4)
      )
      .reduce
  }
