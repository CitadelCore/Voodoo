/* ktlint-disable no-wildcard-imports */
import voodoo.provider.CurseProvider
import voodoo.withDefaultMain

/* ktlint-enable no-wildcard-imports */

fun main(args: Array<String>) = withDefaultMain(
    root = Constants.rootDir.resolve("run"),
    arguments = args
) {
    nestedPack(
        id = "cursefail",
        mcVersion = "1.12.2"
    ) {
        root = rootEntry(CurseProvider) {
            list {
                id(Mod.electroblobsWizardry)
                id(Mod.botania)
                id(Mod.betterBuildersWands)
                id(Mod.bibliocraft)
                id(Mod.toastControl)
            }
        }
    }
}