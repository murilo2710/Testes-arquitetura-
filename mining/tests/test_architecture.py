from pathlib import Path

from pytestarch import Rule, get_evaluable_architecture


def test_analysis_engine_stays_independent_from_delivery_and_parsing() -> None:
    mining_root = Path(__file__).resolve().parents[1]
    architecture = get_evaluable_architecture(mining_root, mining_root / "app")

    Rule().modules_that().are_named("mining.app.analysis_engine").should_not().import_modules_that().are_named(
        [
            "mining.app.main",
            "mining.app.parser",
            "mining.app.graph_miner",
            "mining.app.research_corpus",
        ]
    ).assert_applies(architecture)


def test_research_detector_is_independent_from_api_delivery() -> None:
    mining_root = Path(__file__).resolve().parents[1]
    architecture = get_evaluable_architecture(mining_root, mining_root / "app")

    Rule().modules_that().are_named("mining.app.ipdd_adwin").should_not().import_modules_that().are_named(
        "mining.app.main"
    ).assert_applies(architecture)