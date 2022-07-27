package dataModel

import dataModel.PokemonType.TypeUrl
import dataModel.TypeData.{TypeName, TypeId}
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.Encoder.encodeString
import io.circe.Decoder.decodeString
//import io.circe.generic_extras.auto._
//import io.circe.generic.extras.Configuration
// todo: extras?

case class TypeData(
  id: TypeId,
  name: TypeName,
  damageRelations: DamageRelations
)

object TypeData:
  // todo: one implicit for all opaque types?
  opaque type TypeName = String
  object TypeName:
    given Encoder[TypeName] = Encoder.encodeString
    given Decoder[TypeName] = Decoder.decodeString

  opaque type TypeId = Int
  object TypeId:
    given Encoder[TypeId] = Encoder.encodeInt
    given Decoder[TypeId] = Decoder.decodeInt

  given Encoder[TypeData] =
    Encoder.forProduct3("id", "name", "damage_relations")(x =>
      (x.id, x.name, x.damageRelations)
    )
  given Decoder[TypeData] =
    Decoder.forProduct3("id", "name", "damage_relations")(TypeData.apply)


case class PokemonType(
  name: TypeName,
  url: TypeUrl
)

object PokemonType:
  opaque type TypeUrl = String
  object TypeUrl:
    given Encoder[TypeUrl] = Encoder.encodeString
    given Decoder[TypeUrl] = Decoder.decodeString

  given Encoder[PokemonType] = deriveEncoder
  given Decoder[PokemonType] = deriveDecoder


case class DamageRelations(
  noDamageTo: List[PokemonType],
  halfDamageTo: List[PokemonType],
  doubleDamageTo: List[PokemonType],
  noDamageFrom: List[PokemonType],
  halfDamageFrom: List[PokemonType],
  doubleDamageFrom: List[PokemonType]
)

object DamageRelations:
  given Encoder[DamageRelations] =
    Encoder.forProduct6("no_damage_to", "half_damage_to", "double_damage_to",
    "no_damage_from", "half_damage_from", "double_damage_from")(t =>
      (t.noDamageTo, t.halfDamageTo, t.doubleDamageTo,
        t.noDamageFrom, t.halfDamageFrom, t.doubleDamageFrom)
    )
  given Decoder[DamageRelations] =
    Decoder.forProduct6("no_damage_to", "half_damage_to", "double_damage_to",
      "no_damage_from", "half_damage_from", "double_damage_from")(DamageRelations.apply)
